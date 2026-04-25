const express=require('express'),body=require('body-parser'),{Pool}=require('pg');
const app=express();app.use(body.json());
const pool=new Pool({host:process.env.DB_HOST||'db',user:'postgres',password:'postgres',database:'cheats'});

(async()=>{await pool.query(`CREATE TABLE IF NOT EXISTS profiles(id SERIAL PRIMARY KEY,name TEXT,owner TEXT,cheats JSONB,created_at TIMESTAMP DEFAULT now())`);})();

app.post('/api/perfiles',async(req,res)=>{const {name,owner,cheats}=req.body;
  const r=await pool.query('INSERT INTO profiles(name,owner,cheats) VALUES($1,$2,$3) RETURNING id',[name,owner,cheats]);
  res.json({id:r.rows[0].id});});
app.get('/api/perfiles',async(req,res)=>{const r=await pool.query('SELECT * FROM profiles');res.json(r.rows);});

app.listen(3000,()=>console.log("API en puerto 3000"));