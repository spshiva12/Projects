import React from "react";
import { useQuill } from "react-quilljs";
import "quill/dist/quill.snow.css";
import { useEffect, useRef } from "react";
import axios from "axios";
import { useState } from "react";
import Pagination from "../Pagination";
import { useSelector } from "react-redux";
import { DownloadTableExcel } from "react-export-table-to-excel";
import CopyToClipboard from "react-copy-to-clipboard";
import jsPDF from "jspdf";
import "jspdf-autotable";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import CsvDownloader from "react-csv-downloader";
// import { Document } from "react-pdf";
import { CSVLink } from "react-csv";
import Header from "../header/header";
import Sidebar from "../sidebar/sidebar";
function DailyStatusReport() {
  const { quill, quillRef } = useQuill();
  const [info, setInfo] = useState([]);
  const [data, setData] = useState([]);
  const [empName, setEmpName] = useState("");
  const [projectName, setProjectName] = useState("");
  const [dsrDat, setdsrDat] = useState("");
  const [hrsWorked, sethrsWorked] = useState("");
  const [taskDescription, setTaskDescription] = useState("");
  const [copyText, setCopyText] = useState("");
  const [search, setSearch] = useState();
  const [message, setMessage] = useState();
  const value = useSelector((state) => state.display.api);
  const [api, setApi] = useState(value);
  const [show, setShow] = useState(false);
  const [currentpage, setcurrentpage] = useState(1);
  const [postsperpage, setpostsperpage] = useState(10);
  const [submitcount, setsubmitCount] = useState(0);
  const totalpages = Math.ceil(info.length / postsperpage);
  const pages = [...Array(totalpages + 1).keys()].slice(1);
  const indexofLastpage = currentpage * postsperpage;
  const indexofFirstPage = indexofLastpage - postsperpage;
  const [projectNamesget, setProjectNameget] = useState([]);
  const handleClose = () => setShow(false);
  const prevhandle = () => {
    if (currentpage !== 1) {
      setcurrentpage(currentpage - 1);
    }
  };
  const[open,setOpen]=useState(false)
  const clickShow = () => {
    open? document.body.classList.remove("nav-collapse"): document.body.classList.add("nav-collapse");
    setOpen(!open);
  }
  const nexthandle = () => {
    if (currentpage !== totalpages) {
      setcurrentpage(currentpage + 1);
    }
  };
  const visibleposts = info.slice(indexofFirstPage, indexofLastpage);
  const tableRef = useRef(null);
  const filtered = !search
    ? visibleposts
    : info.filter((person) => {
        return (
          person.empName.toLowerCase().includes(search.toLowerCase()) ||
          person.projectName.toLowerCase().includes(search.toLowerCase()) ||
          person.hrsWorked.toLowerCase().includes(search.toLowerCase()) 
          //  person.dSRReport.toLowerCase().includes(search.toLowerCase()
          //  )
        );
      });

  let str1 = "";

  const getname = (e1,para) => {
     if(para == "empname"){
      if (e1.match("^[a-zA-Z ]*$") != null) {
        setEmpName(e1);
      }
     }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(empName, projectName, dsrDat, hrsWorked, taskDescription);
    if (
      empName.length > 0 &&
      projectName.length > 0 &&
      dsrDat.length > 0 &&
      hrsWorked.length > 0 &&
      taskDescription.length > 0
    ) {
      setShow(true);
      const obj = {
        empName: empName,
        projectName: projectName,
        dsrDat: dsrDat,
        hrsWorked: hrsWorked,
        taskDescription: taskDescription,
      };
      console.log(obj);
       axios.post(`${api}dailystatus/addDSR`,obj).then((resp) => {
        if (resp.status === 200) {
          console.log(resp.data[0].msg, "iam 77 line");
          setMessage(resp.data[0].msg);
        }
        setInfo(resp.data);

            axios.get(`${api}dailystatus/fetchdsr`).then((resp) => {
          setInfo(resp.data);
        
        });
      }).catch((error)=> console.log(error));
      setEmpName("");
      setProjectName("");
      setdsrDat("");
      sethrsWorked("");
      setTaskDescription("");
    } else {
      setsubmitCount(1);
    }
  };
  const gethours = (e1,para) => {
   
    if(para == "hours" ){
        if(e1.length<=5){
          sethrsWorked(e1.match('^[0-9:]+$'));
        }
    }

  };

  console.log(dsrDat, "iam dsrDat");
  useEffect(() => {
    if (quill) {
      quill.on("text-change", () => {
        let val = quillRef.current.firstChild.innerHTML;
        val = val.split("<p>").join("").split("</p>").join("");
        console.log(val);
        // console.log(quillRef.getText())

        setTaskDescription(val);
      });
    }
  }, [quill]);

  const csvReport = {
    data: filtered,
    headers: [
      { label: "Employee Name", key: "empName" },
      { label: "Project Name", key: "projectName" },
      { label: "Hours Worked", key: "hrsWorked" },
      { label: "task Description", key: "task" },
    ],
    filename: "DSR-Report.csv",
  };

  useEffect(() => {
    axios.get(`${api}dailystatus/fetchdsr`).then((resp) => {
      setInfo(resp.data);
      // console.log(Object.keys(resp.data[0]));
      // setHeaders()
    });
    axios
      .get(`${api}dailystatus/getProjects`)
      .then((resp) => setProjectNameget(resp.data));
  }, []);
  console.log(info);
 
  const exportPDF = () => {
    const unit = "pt";
    const size = "A4"; // Use A1, A2, A3 or A4
    const orientation = "portrait"; // portrait or landscape

    const marginLeft = 40;
    const doc = new jsPDF(orientation, unit, size);

    doc.setFontSize(15);

    const title = "My Awesome Report";
    const headers = [
      ["EmployeeName", "ProjectName", "Houers-Worked", "Task-Description"],
    ];
    const data = filtered.map((elt) => [
      elt.empName,
      elt.projectName,
      elt.hrsWorked,
      elt.taskDescription,
    ]);

    let content = {
      startY: 50,
      head: headers,
      body: data,
    };

    doc.text(title, marginLeft, 40);
    doc.autoTable(content);
    doc.save("report.pdf");
  };
  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  // btnCopyTable.addEventListener('click', () => copyEl(elTable));
  const Print = () => {
    //console.log('print');
    let printContents = document.getElementById("printablediv").innerHTML;
    let originalContents = document.body.innerHTML;
    document.body.innerHTML = printContents;
    window.print();
    document.body.innerHTML = originalContents;
  };
  console.log(message, "iam 182 line");
  return (
    <div>
      <Header data={clickShow}/>
      <Sidebar open={open} />
      <main id="main" className="">
        <section className="col-md-12" style={{ minHeight: "480px" }}>
          <div className="d-flex align-items-center justify-content-between">
            <h4>Daily Status Reports(DSR)</h4>
          </div>
          <form>
            <div className="row">
              <div className="col-lg-3">
                <div className="mb-2">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Employee name"
                    required
                    autoComplete="off"
                    value={empName}
                    style={
                      submitcount == 1 && empName.length == 0
                        ? { border: "solid red 1px" }
                        : null
                    }
                    onChange={(e) => {
                      getname(e.target.value, "empname");
                    }}
                  />
                </div>
              </div>
              <div className="col-lg-3">
                <div className="mb-2">
                  <select
                    className="form-select"
                    aria-label="Defalt select example"
                    value={projectName}
                    style={
                      submitcount == 1 && projectName.length == 0
                        ? { border: "solid red 1px" }
                        : null
                    }
                    onChange={(e) => setProjectName(e.target.value)}
                  >
                    <option>Select Project</option>
                    {projectNamesget.map((e,index) => (
                      <option key={index} value={e.projectName}>{e.projectName}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="col-lg-3">
                <div className="mb-2">
                  <div className="d-grid gap-2 col-6 mx-auto">
                    <button
                      className="btn btn-primary"
                      type="button"
                      onClick={handleSubmit}
                    >
                      Submit
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-lg-2">
                <div className="mb-2">
                  <label className="form-label">Date</label>
                  <input
                    type="date"
                    className="form-control"
                    placeholder="Date"
                    required
                    autoComplete="off"
                    value={dsrDat}
                    style={
                      submitcount === 1 && dsrDat.length === 0
                        ? { border: "solid red 1px" }
                        : null
                    }
                    onChange={(e) => setdsrDat(e.target.value)}
                  />{" "}
                </div>
              </div>
              <div className="col-lg-2">
                <div className="mb-2">
                  <label className="form-label">Hours Worked</label>
                  <input
                    type="/^[A-Za-z\s]*$/"
                    className="form-control"
                    placeholder="eg:04:10"
                    required
                    autoComplete="off"
                    value={hrsWorked}
                    style={
                      submitcount === 1 && hrsWorked.length === 0
                        ? { border: "solid red 1px" }
                        : null
                    }
                    onChange={(e) => {
                      sethrsWorked(e.target.value, "hours");
                    }}
                  />
                </div>
              </div>
              <div className="col-lg-8">
                <div className=" form-group">
                  <div className="desc">Task Description</div>
                  <div
                    className="quill-editor-default"
                    style={
                      submitcount == 1 && taskDescription.length == 0
                        ? { border: "solid red 1px" }
                        : null
                    }
                  >
                    <div>
                      <div>
                        <div ref={quillRef} />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </form>
          <div className="row py-5">
            <div className="col-md-12">
              <div className="card mb-2">
                <div className="card-body">
                  <form>
                    <div className="row py-3 mb-5">
                      <div className="col-lg-6">
                        <div className="mb-2">
                          <input
                            type="Search"
                            className="form-control"
                            placeholder="search"
                            required
                            autoComplete="off"
                            value={search}
                            onChange={handleSearch}
                          />
                        </div>
                      </div>
                      <div className="col-lg-4" style={{ textAlign: "right" }}>
                        <CSVLink {...csvReport}>
                          <button
                            type="button"
                            className="btn btn-outline-secondary me-md-2 "
                          >
                            CSV
                          </button>
                        </CSVLink>
                        <DownloadTableExcel
                          filename="users table"
                          sheet="users"
                          currentTableRef={tableRef.current}
                        >
                          <button
                            type="button"
                            className="btn btn-outline-secondary me-md-2"
                          >
                            Excel
                          </button>
                        </DownloadTableExcel>
                        <button
                          type="button"
                          className="btn btn-outline-secondary me-md-2"
                          onClick={exportPDF}
                        >
                          PDF
                        </button>
                        <button
                          type="button"
                          className="btn btn-outline-secondary me-md-2"
                          onClick={Print}
                        >
                          Print
                        </button>
                      </div>
                      <div className="col-lg-2">
                        Show
                        <select
                          className="btn btn-outline-secondary"
                          aria-label="Default select example"
                          onChange={(e) => setpostsperpage(e.target.value)}
                        >
                          <option value="1">1</option>
                          <option value="3">3</option>
                          <option value="10">10</option>
                          <option value="25">25</option>
                          <option value="50">50</option>
                          <option value="100">100</option>
                        </select>
                      </div>
                      <div id="printablediv">
                        <table className="table" ref={tableRef}>
                          <thead>
                            <tr
                              className="table-primary"
                              style={{ textAlign: "left" }}
                            >
                              <th scope="col" style={{ width: "120px" }}>
                                EmployeeName
                              </th>
                              <th scope="col" style={{ width: "120px" }}>
                                ProjectName
                              </th>
                              <th scope="col" style={{ width: "120px" }}>
                                Hours
                              </th>
                              <th scope="col" style={{ width: "120px" }}>
                                TaskDescription
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {filtered.map((e, i) => (
                              <tr
                                key={i}
                                style={{
                                  textAlign: "left",
                                  wordWrap: "break-word",
                                }}
                              >
                                <td scope="row"key={i} >{e.empName}</td>
                                <td>{e.projectName}</td>
                                <td>{e.hrsWorked}</td>
                                <td>{e.taskDescription}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                        {/* <hr className="mt-4 mb-4" /> */}
                        {[
                          ...Array(
                            Math.ceil(
                              (!search ? info.length : filtered.length) /
                                postsperpage
                            ) + 1
                          ).keys(),
                        ].slice(1).length > 1 ? (
                          <nav
                            aria-label="..."
                            style={{ paddingBottom: "250px" }}
                          >
                            
                            <ul className="pagination">
                              
                              <li className="page-item ">
                                
                                <span
                                  className={`page-link ${
                                    currentpage <= 1 && "disabled"
                                  }`}
                                  style={{ cursor: "pointer" }}
                                  onClick={prevhandle}
                                >
                                   Previous
                                 
                                </span>
                                
                              </li>
                             
                              {[
                                ...Array(
                                  Math.ceil(
                                    (!search ?  info.length : filtered.length) /
                                      postsperpage
                                  ) + 1
                                ).keys(),
                              ]
                                .slice(1)
                                .map((e, i) => (
                                  <li className="page-item" key={i}>
                                    
                                    <a
                                      className={`page-link ${
                                        currentpage === e && "disabled"
                                      }`}
                                      style={{ cursor: "pointer" }}
                                      onClick={() => setcurrentpage(e)}
                                    >{` ${e}`}</a>
                                   
                                  </li>
                                ))}
                              
                              <li className="page-item">
                                
                                <a
                                  className={`page-link ${
                                    currentpage ===
                                      [
                                        ...Array(
                                          Math.ceil(
                                            (!search
                                              ?  info.length
                                              : filtered.length) / postsperpage
                                          ) + 1
                                        ).keys(),
                                      ].slice(1).length && "disabled"
                                  }`}
                                  style={{ cursor: "pointer" }}
                                  onClick={nexthandle}
                                >
                                  Next
                                  
                                </a>
                               
                              </li>
                              
                            </ul>
                           
                          </nav>
                        ) : (
                          <div style={{ paddingBottom: "100px" }}></div>
                        )}
                      </div>
                    </div>
                  </form>
                </div>
                <div className="card-footer  text-end">
                  <a href="#">View all</a>
                </div>
              </div>
            </div>
          </div>
          <div>
            <Modal show={show}>
              <Modal.Body>{message}</Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                  Close
                </Button>
              </Modal.Footer>
            </Modal>
          </div>
        </section>
      </main>
     
    </div>
  )
}



export default DailyStatusReport;
