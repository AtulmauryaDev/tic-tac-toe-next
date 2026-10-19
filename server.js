'use strict';
const http = require('http');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const ROOT = path.join(__dirname, 'web');
const PORT = Number(process.env.PORT || 3000);
const rooms = new Map();
const mime = { '.html':'text/html; charset=utf-8', '.css':'text/css; charset=utf-8', '.js':'text/javascript; charset=utf-8', '.json':'application/json; charset=utf-8', '.svg':'image/svg+xml' };
function json(res, status, data) { res.writeHead(status, {'Content-Type':'application/json; charset=utf-8','Cache-Control':'no-store','Access-Control-Allow-Origin':'*'}); res.end(JSON.stringify(data)); }
function readBody(req) { return new Promise((resolve,reject)=>{ let data=''; req.on('data',c=>{data+=c; if(data.length>10000) req.destroy();}); req.on('end',()=>{try{resolve(data?JSON.parse(data):{});}catch(e){reject(e);}}); req.on('error',reject); }); }
function win(board) { const lines=[[0,1,2],[3,4,5],[6,7,8],[0,3,6],[1,4,7],[2,5,8],[0,4,8],[2,4,6]]; for(const line of lines) if(board[line[0]] && board[line[0]]===board[line[1]] && board[line[1]]===board[line[2]]) return {winner:board[line[0]],line}; if(board.every(Boolean)) return {winner:'draw',line:[]}; return {winner:null,line:[]}; }
function snapshot(room, playerId) { return {code:room.code,board:room.board,current:room.current,winner:room.winner,line:room.line,players:room.players.length,role:room.players.find(p=>p.id===playerId)?.symbol || null,updatedAt:room.updatedAt}; }
const server = http.createServer(async (req,res)=>{
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  if(req.method==='OPTIONS'){res.writeHead(204,{'Access-Control-Allow-Origin':'*','Access-Control-Allow-Headers':'Content-Type','Access-Control-Allow-Methods':'GET,POST,OPTIONS'});return res.end();}
  if(url.pathname.startsWith('/api/')) {
    try {
      if(req.method==='POST' && url.pathname==='/api/create') {
        let code; do { code=crypto.randomBytes(3).toString('hex').toUpperCase(); } while(rooms.has(code));
        const id=crypto.randomUUID(); const room={code,board:Array(9).fill(null),current:'X',winner:null,line:[],players:[{id,symbol:'X'}],updatedAt:Date.now()}; rooms.set(code,room); return json(res,200,{...snapshot(room,id),playerId:id});
      }
      if(req.method==='POST' && url.pathname==='/api/join') {
        const body=await readBody(req), code=String(body.code||'').trim().toUpperCase(), room=rooms.get(code);
        if(!room) return json(res,404,{error:'Room not found. Check the code or create a new room.'});
        if(room.players.length>=2) return json(res,409,{error:'This room already has two players.'});
        const id=crypto.randomUUID(); room.players.push({id,symbol:'O'}); room.updatedAt=Date.now(); return json(res,200,{...snapshot(room,id),playerId:id});
      }
      if(req.method==='GET' && url.pathname==='/api/state') {
        const room=rooms.get(String(url.searchParams.get('room')||'').toUpperCase()), id=url.searchParams.get('player');
        if(!room) return json(res,404,{error:'Room not found. It may have been cleared after a server restart.'});
        if(!room.players.some(p=>p.id===id)) return json(res,403,{error:'This player is not part of the room.'}); return json(res,200,snapshot(room,id));
      }
      if(req.method==='POST' && url.pathname==='/api/move') {
        const body=await readBody(req), room=rooms.get(String(body.room||'').toUpperCase());
        if(!room) return json(res,404,{error:'Room not found.'});
        const player=room.players.find(p=>p.id===body.playerId);
        if(!player) return json(res,403,{error:'You are not a player in this room.'});
        if(room.players.length<2) return json(res,409,{error:'Waiting for the second player to join.'});
        if(room.winner) return json(res,409,{error:'This round has ended. Start a new round.'});
        if(player.symbol!==room.current) return json(res,409,{error:'It is the other player’s turn.'});
        const index=Number(body.index); if(!Number.isInteger(index)||index<0||index>8||room.board[index]) return json(res,400,{error:'That square is not available.'});
        room.board[index]=player.symbol; const result=win(room.board); room.winner=result.winner; room.line=result.line; if(!room.winner) room.current=room.current==='X'?'O':'X'; room.updatedAt=Date.now(); return json(res,200,snapshot(room,body.playerId));
      }
      if(req.method==='POST' && url.pathname==='/api/reset') {
        const body=await readBody(req), room=rooms.get(String(body.room||'').toUpperCase()); if(!room) return json(res,404,{error:'Room not found.'});
        if(!room.players.some(p=>p.id===body.playerId)) return json(res,403,{error:'You are not a player in this room.'});
        room.board=Array(9).fill(null); room.current='X'; room.winner=null; room.line=[]; room.updatedAt=Date.now(); return json(res,200,snapshot(room,body.playerId));
      }
      return json(res,404,{error:'API route not found.'});
    } catch(e) { return json(res,400,{error:'Invalid request.'}); }
  }
  let pathname=decodeURIComponent(url.pathname); if(pathname==='/') pathname='/index.html';
  const file=path.resolve(ROOT,'.'+pathname); if(!file.startsWith(ROOT+path.sep) && file!==ROOT) {res.writeHead(403);return res.end('Forbidden');}
  fs.readFile(file,(err,content)=>{if(err){res.writeHead(404,{'Content-Type':'text/plain; charset=utf-8'});return res.end('Not found');}res.writeHead(200,{'Content-Type':mime[path.extname(file)]||'application/octet-stream','X-Content-Type-Options':'nosniff','Cache-Control':'no-cache'});res.end(content);});
});
server.listen(PORT,'0.0.0.0',()=>console.log(`Tic-Tac-Toe Next is running at http://localhost:${PORT}`));
