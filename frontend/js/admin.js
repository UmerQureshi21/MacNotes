/**
 * This is the js file of the admin page
 * Authors: Marko Kosoric
 */

import { createDeleteButton } from './deleteFileHandler.js';

document.addEventListener("DOMContentLoaded", () => {
    
    // Check if the user is an admin before doing anything else
    fetch("server/checkAdmin.php")
    .then(response => response.json())
    .then(data => {
      if (!data.is_admin) {
        // Not an admin – redirect
        window.location.href = "adminlogin.html";
        return;
      }


      // GRID/LIST VIEW CODE
      let gridViewBtn = document.getElementById("gridView");
      let listViewBtn = document.getElementById("listView");
      let resultsContainer = document.querySelector(".results-container");
      resultsContainer.innerHTML = "Notes Will Be Displayed Here";
    
      // Set classes for grid view
      gridViewBtn.addEventListener("click", () => {
        resultsContainer.classList.add("grid-view");
        resultsContainer.classList.remove("list-view");
        gridViewBtn.classList.add("active");
        listViewBtn.classList.remove("active");
      });
    
      // Set classes for list view
      listViewBtn.addEventListener("click", () => {
        resultsContainer.classList.add("list-view");
        resultsContainer.classList.remove("grid-view");
        listViewBtn.classList.add("active");
        gridViewBtn.classList.remove("active");
      });
    
      // Elements' IDs and filter variables
      let search_bar = document.getElementById("search_bar");
      let results = [];
      let formData;
      let min_size_slider = document.getElementById("min_size_slider");
      let max_size_slider = document.getElementById("max_size_slider");
      let minrangelabel = document.getElementById("minrangelabel");
      let maxrangelabel = document.getElementById("maxrangelabel");
      let coursecode_filter = document.getElementById("coursecode_filter");

      let coursecodes = [];
      let orderbyoption = "`download-number`";
    
      //Get coursecodes
      fetch("server/getCourseCodeFromAdmins.php", {
        method: "POST"
      })
        .then(response => response.json())
        .then(data => {
          console.log(data);
          coursecodes = data.courseCodes ?? [];
      
          // Clear old checkboxes
          coursecode_filter.innerHTML = "";
      
          // Add new checkboxes
          coursecodes.forEach(code => {
            let option = document.createElement("div");
            option.classList.add("checkbox-wrapper");
            option.innerHTML = `
                <input
                  type="checkbox"
                  id="${code}"
                  checked
                  value="${code}"
                  class="coursecodecheckboxes"
                />
                <label for="${code}">${code}</label>
            `;
            coursecode_filter.appendChild(option);
          });
          
          // Edits coursecode array according to what the user selects or not
          for (let coursecodecheckbox of document.getElementsByClassName(
            "coursecodecheckboxes"
          )) {
            coursecodecheckbox.addEventListener("click", function (event) {
              if (this.checked) {
                coursecodes.push(this.value);
              } else {
                let copy = [];
                for (let c of coursecodes) {
                  if (c != this.value) {
                    copy.push(c);
                  }
                }
                coursecodes = copy;
              }
              getNotes();
            });
          }
      
          // Submit initial filtered search
          submitForm(search_bar.value, { max: 100, min: 0 }, coursecodes, orderbyoption);
        })
        .catch(error => console.error("Getting course code from admins error:", error));
      
    
      /**
       *
       * @param {*} rows |  the data sent from the server which is a list of objects
       */
      function displayResults(rows) {
        results = rows;
    
        resultsContainer.innerHTML = "";
        if (results.length === 0) {
          console.log("Nothing to show");
          resultsContainer.innerHTML = "No Results";
        } else {
          for (let row of results) {
            let containerDiv = document.createElement("div"); // create container div
    
            containerDiv.classList.add("result-card");
            let courseTitleAndNoteName = document.createElement("h3");
            courseTitleAndNoteName.innerHTML =
              row.coursecode + ": " + row.filetitle;
            let courseDescription = document.createElement("p");
            courseDescription.innerHTML = row.description;
    
            let resultInfoDiv = document.createElement("div");
            resultInfoDiv.classList.add("result-info");
    
            let ratingSpan = document.createElement("span");
            ratingSpan.classList.add("rating");
            let starSymbol = document.createElement("i");
            starSymbol.classList.add("ri-star-fill");
            ratingSpan.appendChild(starSymbol);
            let ratingNumber = document.createElement("span");
            ratingNumber.setAttribute("id", "ratingNumber");
            ratingNumber.innerHTML = row.rating;
            ratingSpan.appendChild(ratingNumber);
    
            let downloadSpan = document.createElement("span");
            downloadSpan.classList.add("downloads");
            let downloadSymbol = document.createElement("i");
            downloadSymbol.classList.add("ri-download-2-line");
            downloadSpan.appendChild(downloadSymbol);
            let downloadNumber = document.createElement("span");
            downloadNumber.setAttribute("id", "downloadNumber");
            downloadNumber.innerHTML = row["download-number"];
            downloadSpan.appendChild(downloadNumber);
    
            resultInfoDiv.appendChild(ratingSpan);
            resultInfoDiv.appendChild(downloadSpan);
    
            // adding all above elements to container div
            containerDiv.appendChild(courseTitleAndNoteName);
            containerDiv.appendChild(courseDescription);
            containerDiv.appendChild(resultInfoDiv);
            
            //create delete button
            let deleteButton = createDeleteButton(row.filename, () => {
              containerDiv.remove();
              getNotes();
            });
            deleteButton.classList.add("delete-btn");
            containerDiv.appendChild(deleteButton);
    
            // The view button gets an href link with some of the details of the file in the URL so
            // that the page for viewing the file has the approporate data to shoe
            let viewbutton = document.createElement("a");
            viewbutton.innerHTML = "Preview";
            viewbutton.classList.add("view-btn");
            viewbutton.setAttribute(
              "href",
              `adminsearchfiledetails.html?filename=${encodeURIComponent(
                row.filename
              )}&filetitle=${encodeURIComponent(
                row.filetitle
              )}&filedescription=${encodeURIComponent(
                row.description
              )}&coursecode=${encodeURIComponent(row.coursecode)}`
            );
    
            viewbutton.setAttribute("target", "");
            containerDiv.appendChild(viewbutton);
    
            // adding container div to result container
            resultsContainer.appendChild(containerDiv);
            console.log(coursecodes);
          }
        }
      }
    
      /**
       * Sends a post request to a server and returns the jsonified data
       */
      async function searchDatabase() {
        const response = await fetch("server/search.php", {
          method: "POST",
          body: formData,
        });
    
        const data = await response.json(); // Wait for JSON parsing
    
        // DATA IS OF THE FORM [ERROR: ERROR_MESSAGE, RESULT: ROWS]
        if (data.error === "") {
          displayResults(data.result);
          console.log(data.result);
        } else {
          console.log(data.error);
        }
      }
    
      /**
       * Creates a form data object that it sends to the server and then displays the results on the screen
       * @param {*} filename | the value of the searchbar
       * @param {*} coursecodes | the coursecodes that the user wants to search for
       * @param {*} orderbyoption | the order that the user wants the files to be displayed (most downloaded, alphabetically, etc)
       * @param {*} filesizerange | an object of the form {Max: value, Min:value}
       */
      function submitForm(filename, filesizerange, coursecodes, orderbyoption) {
        formData = new FormData();
        formData.append("query", filename);
        formData.append("filesizefilter", JSON.stringify(filesizerange)); //formData object does not support objects, so i make it into string
        formData.append("coursecodefilter", JSON.stringify(coursecodes));
        formData.append("orderbyoption", orderbyoption);
        searchDatabase();
      }
    
      /**
       * Calls the submitForm functions only if the value of the max slider is actually greater than the value of the min
       */
      function getNotes() {
        if (Number(max_size_slider.value) > Number(min_size_slider.value)) {
          submitForm(
            search_bar.value,
            { max: max_size_slider.value, min: min_size_slider.value },
            { coursecodes: coursecodes }, 
            orderbyoption
          );
        } else {
          resultsContainer.innerHTML =
            "Max file size is smaller than Min file size!!";
          console.log("Max: ", max_size_slider.value);
          console.log("Min: ", min_size_slider.value);
        }
      }
    
      // Calling getNotes to IDs
      search_bar.addEventListener("input", function (event) {
        getNotes();
      });
    
      min_size_slider.addEventListener("input", function (event) {
        minrangelabel.innerHTML = this.value;
        if (parseInt(min_size_slider.value) > parseInt(max_size_slider.value)) {
          min_size_slider.value = max_size_slider.value;
        }
        getNotes();
      });
    
      max_size_slider.addEventListener("input", function (event) {
        maxrangelabel.innerHTML = this.value;
        if (parseInt(max_size_slider.value) < parseInt(min_size_slider.value)) {
          max_size_slider.value = min_size_slider.value;
        }
        getNotes();
      });

      // Sets the option that the user wants to order by
      document.getElementById("sortSelect").addEventListener("change", function () {
        selectedValue = this.value;
        if (selectedValue === "Highest Rated") {
          orderbyoption = "rating";
        } else if (selectedValue === "Most Downloaded") {
          orderbyoption = "`download-number`";
        } else if (selectedValue === "Newest") {
          orderbyoption = "upload_time";
        } else if (selectedValue === "Name") {
          orderbyoption = "filetitle";
        }
        getNotes();
      });
      // End of admin-only logic
    })
    .catch(error => {
      console.error("Error checking admin:", error);
      window.location.href = "adminlogin.html";
    });

  });
  