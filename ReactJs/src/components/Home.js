import React from 'react'
import "./Home.css"
import { useState } from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import DailyStatusReport from './dsr/DailyStatusReport';
import Footer from './footer/footer';
import Header from './header/header';
import Sidebar from './sidebar/sidebar';
import Summary from './summary/Summary';
import axios from 'axios';
import { useEffect } from 'react';
import { api } from '../Redux/Action';

const Home = () => {
    

    const [open, setOpen] = useState(false)
    const clickShow = () => {
        open ? document.body.classNameList.remove("nav-collapse") : document.body.classNameList.add("nav-collapse");
        setOpen(!open);
    };
    const [pass, setPass] = useState("");
    const [email, setEmail] = useState("");
    const[employee,setEmployee]=useState([]);
    const navigate=useNavigate();
  useEffect(()=>{
    addEmployee()
  },[])
    const addEmployee=()=>{
         axios.get("http://localhost:9091/dailystatus/getEmp").then(resp=>{setEmployee(resp.data)}).catch((error)=>console.log(error+ " "+ "error"))
    }
    
const handleSubmit=(e)=>{
  e.preventDefault();
  console.log(email+" "+pass)
  console.log(employee)
  employee.map((e,i)=>{
      if(e.email===email && e.password===pass)
      {
        navigate("/dsr")
      }
  })
}
    return (
        
        <div className="container mt-5">
             
           <form>
            <div id="card" className="card">
              <div className="card-body">
  <div className="form-group">
    <label for="exampleInputEmail1">Email address</label>
    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email"onChange={(e)=>{setEmail(e.target.value)}}/>
    
  </div>
  <div className="form-group">
    <label for="exampleInputPassword1">Password</label>
    <input type="password" className="form-control" id="exampleInputPassword1" placeholder="Password" onChange={(e)=>{setPass(e.target.value)}}/>
  </div>
  <div className="form-group form-check">
    <input type="checkbox" className="form-check-input" id="exampleCheck1"/>
    <label className="form-check-label" for="exampleCheck1">Check me out</label>
  </div>
  <button type="submit" onClick={(e)=>{handleSubmit(e)}} className="btn btn-primary">Submit</button>
  </div>
  </div>
</form>
<img className='image' src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAABR1BMVEX///8dYqfvr07///3//v////n///seYaf///YdYqXvrk/8//////MoY6DS5/n///eTt9H1/v1wmrv//+0ZWp2hx9norFH///AaXJ/nrlkhXpvfq17lt278+eTx2J6mwNbx+f/otGXevYX/99p6mLDx3rflvHvd9/j//+X79eTfp1Tw2KztwoXzz5XbwYn368whV4zP3+pAdan/+NnqyIzmunSBrNfnypf45brcrGHw5sjw3brkzKH//Mr567vYtX3w16ZhjayGqMBehaPvvnK0y9pOd5o5Zo0YUo4rXYkbU47f6u5unMejxuSGuuksaqRHboxKeKXP3udSi8JaiLC1ydbV6vy72fK10/e82+psi6CXssXl1LNnlr6Eo8NdgqXTya/a3NJMf61FfbZNhcHQnlbOsH301pL75q/+98Do4c/WwJTw0IdKkPKkAAAV+UlEQVR4nO1c/3/aRpoWGSQkJCITqEAg2cgUbMCs+BoJQxPZsRMMyXK4TrvepM6e93rNbfv//3zvzEgggYTdbT7X3Z6e1LaQRjPvM++XmXlnKMPEiBEjRowYMWLEiBEjRowYMWLEiBEjRowYMWLEiBEjRowYMWLEiPH/GgjRP7+zGL8diJL49yfC/EFIMEn498cAQn8MjTCcJHG/twxfAqjx0uJ/1QuBD/STGPIoCiIptWHPYVbxgKWsH9MrZI72+UeJ4LWNECtLklTAPwVJ3hHzgqL8eguO8l7XG9zf7k3ZHrXYx9WLSKzmdXPc2vfgtExTlxF99rAcYR0f1dguQUIK8Pvl2uN9RNZrzi+GqvigGgNnrIf1hZiNhBiQCkHRyu5SIWxwj47Htdp4bFZ1ninUjdNHKJ0U4U1nqCrpJwGk04piDFrVbSqVD2fPQnF2VglUjJjKeUjRV9fhsmBNo9zYGlyozWZTUVVjOLAcx7gwH+aBTUcyBxeKApJvEMG/FHVoVdkN98xevnr95qozBeQJivk8XHeuXp9ltzi/xgWLeRfF6ezqzat5pDh8bWBga9A0DXoW96WiKsPGI4gwbB9eTa8k3yQDdWmOzgaYiNn5fH593e3evs0nMFJC791tt3s9r4gbDiRWcME/51MJisUtlJpHmRanWyCLevLCrFarpj1oKkSKeiSRJP6P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" alt="img" />

        </div>

    )
}

export default Home

