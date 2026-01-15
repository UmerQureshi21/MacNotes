/**
 * This is the js file of the file preview page
 * Authors: Umer Qureshi & Sepanta Kamali
 */
import { createDeleteButton } from './deleteFileHandler.js';

window.addEventListener("load", function (event) {
  // Check if the user is an admin before doing anything else
  fetch("server/checkAdmin.php")
  .then(response => response.json())
  .then(data => {
    if (!data.is_admin) {
      // Not an admin – redirect
      window.location.href = "adminlogin.html";
      return;
    }
    const params = new URLSearchParams(window.location.search);
    const filename = params.get("filename");
    const filetitle = params.get("filetitle");
    const filedescription = params.get("filedescription");
    const coursecode = params.get("coursecode");
    const macID = params.get("macID");
    const downloadBtn = document.getElementById("preview-download-btn");


    document.getElementById("filedisplay").src = "uploads/" + filename;
    document.getElementById("filetitle").innerHTML = "Title: " + filetitle;
    document.getElementById("filedescription").innerHTML =
      "Description: " + filedescription;
    document.getElementById("coursecode").innerHTML =
      "Course Code: " + coursecode;

    let deleteButtonWrapper = document.getElementById("delete-button-wrapper");
    let deleteButton = createDeleteButton(filename, () => {
      alert("File deleted. Returning to admin page.");
      window.location.href = "admin.html";
    });
    deleteButton.setAttribute("id", "delete-btn");
    deleteButton.classList.add("btn", "btn-primary", "download-btn");
    deleteButton.innerHTML = `<i class="ri-delete-bin-line"></i> <span>Delete</span>`;
    deleteButtonWrapper.appendChild(deleteButton);
    

    setInterval(function(){
      // we need to send a fetch request to the database to get the number of downloads
      let formData = new FormData();
      formData.append("filename", filename);

      fetch("server/filePreview.php", {
        method: "POST",
        body: formData,
      })
        .then(response => response.json())
        .then(data => {
          // update the download count on the page
          const count = data.download_count ?? 0; // set it to zero if any errors
          document.getElementById("number-downloads-preview").innerHTML = count;
        })
        .catch(error => { // console error if faced any errors
          console.error("Error fetching download count:", error);
        });
    }, 100);

    downloadBtn.addEventListener("click", function(event){
      // trigger the browser to download file
      const a = document.createElement("a");
      a.href = `uploads/${filename}`;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);

      // update the number of downloads in the databse
      let newFromData = new FormData();
      newFromData.append("filename", filename);

      fetch("server/download.php", {
        method: "POST",
        body: newFromData,
      })
        .catch((error) => console.error("Error:", error));
    })

    let filenameFormData = new FormData();
    filenameFormData.append("filename", filename);

    fetch("server/getUserFromFileName.php", {
      method: "POST",
      body: filenameFormData
    })
      .then(response => response.json())
      .then(data => {
        let username = data.user_name;
        console.log(username);
        let usernameBox = document.getElementById("username");
        usernameBox.innerHTML = `Username: ${username}`;
      })
      .catch((error) => console.error("Error:", error));

      // define the report button
      const reportBtn = document.getElementById("report-btn");

      // we add an event listener for when the report button is clicked
      // it redirects the user to report.html and sends the search parameters

      reportBtn.addEventListener("click", function(event){
        const a = document.createElement("a");
        a.href = `report.html?filename=${encodeURIComponent(
              filename
            )}&filetitle=${encodeURIComponent(
              filetitle
            )}&coursecode=${encodeURIComponent(
              coursecode
            )}`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      });

    // -------------------------------------- Ratings---------------------------------------------

    // first we need to check if the user has rated thh file before or not 
    // if they have already rated this file before they won't see the rating section
    // if they haven't, they can see the rating section and they can rate

    let rateFormData = new FormData()
    rateFormData.append("filename", filename);

    fetch("server/ratingHistory.php", {
      method: "POST",
      body: rateFormData,
    })
      .then(response => response.json())
      .then(data => {
        isRated = data.rated;
    
        // define the submit rating button 
        const submitRatingBtn = document.getElementById("submit-rating-btn");
        const submitRatingFeedback = document.getElementById("rating-submission-feedback");
        const ratingSection = document.getElementById("rating-section");
    
        if (!isRated) {
          let starClicked = 0;
    
          for (let i = 1; i <= 5; i++) {
            let star = document.getElementById("star" + i);
            star.addEventListener("click", function () {
              starClicked = i;
    
              for (let j = 1; j <= 5; j++) {
                let currentStar = document.getElementById("star" + j);
    
                if (j <= i) {
                  currentStar.classList.remove("ri-star-line");
                  currentStar.classList.add("ri-star-fill");
                } else {
                  currentStar.classList.remove("ri-star-fill");
                  currentStar.classList.add("ri-star-line");
                }
              }
            });
          }
    
          submitRatingBtn.addEventListener("click", function (event) {
            if (starClicked == 0) {
              event.preventDefault();
              submitRatingFeedback.style.color = "red";
              submitRatingFeedback.innerHTML = "Please rate the document first!";
            } else {
              let ratingFormData = new FormData();
              ratingFormData.append("rating", starClicked);
              ratingFormData.append("filename", filename);
    
              fetch("server/updateRating.php", {
                method: "POST",
                body: ratingFormData,
              })
                .then((response) => response.text())
                .then((data) => {
                  submitRatingFeedback.innerHTML = data;
                  submitRatingFeedback.style.color = "green";
                  submitRatingFeedback.style["margin-top"] = "10px";
                  ratingSection.style.display = "none";
                })
                .catch((error) => console.error("Error:", error));
            }
          });
        } else {
          ratingSection.style.display = "none";
        }
      });

      // define the average rating span
      const averageRatingSpan = document.getElementById("average-rate");

      // set interval to update the average rating constantly
      setInterval(function(){
        let getAveRateFormData = new FormData();
        getAveRateFormData.append("filename", filename);

        fetch("server/getAverageRating.php", {
          method: "POST",
          body: getAveRateFormData,
        })
          .then(response => response.json())
          .then(data => {
            let averageRating = data.rating;
            averageRatingSpan.innerHTML = averageRating;

            let floorAverageRating = Math.floor(averageRating);

            for (let i = 1; i <= 5; i++){
              let sstar = document.getElementById("sstar" + i);
              if (i <= floorAverageRating){
                sstar.classList.remove("ri-star-line");
                sstar.classList.remove("ri-star-half-fill");
                sstar.classList.add("ri-star-fill");
              }

              else if ((i - floorAverageRating  == 1) && (floorAverageRating < averageRating)){
                sstar.classList.remove("ri-star-fill");
                sstar.classList.remove("ri-star-line");
                sstar.classList.add("ri-star-half-fill")
              }

              else{
                sstar.classList.remove("ri-star-half-fill");
                sstar.classList.remove("ri-star-fill");
                sstar.classList.add("ri-star-line");
              }
            }
          })
          .catch((error) => console.error("Error:", error));
      }, 100)
  }) 
  .catch(error => {
  console.error("Error checking admin:", error);
  window.location.href = "adminlogin.html";
  });
});


  // FAILED ATTEMPT AT GETTING PDF ON CANVAS ELEMENT FOR NICEFR LOOKING PREVIEW FUNCTOINALITY


  /*
  <!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>test</title>
      <style>
        body {
          background-color: blue;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        canvas {
        }
      </style>
    </head>
    <body>
      <div id="pdf-container">
        <h1>Hello</h1>
        <canvas id = "the-canvas"></canvas>
      </div>
      <script src="https://cdn.jsdelivr.net/npm/pdfjs-dist@5.1.91/wasm/openjpeg_nowasm_fallback.min.js"></script>
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/pdfjs-dist@5.1.91/web/pdf_viewer.min.css">


      <script>
        //https://cdnjs.com/libraries/pdf.jsge
        //https://mozilla.github.io/pdf.js/examples/
        let loadingTask = pdfjsLib.getDocument("uploads/A4.pdf");
        loadingTask.promise.then(function (pdf) {
          // you can now use *pdf* here
          pdf.getPage(1).then(function (page) {
            // you can now use *page* here
            let scale = 1.5;
            let viewport = page.getViewport({ scale: scale });
            // Support HiDPI-screens.
            let outputScale = window.devicePixelRatio || 1;

            let canvas = document.getElementById("the-canvas");
            let context = canvas.getContext("2d");

            canvas.width = Math.floor(viewport.width * outputScale);
            canvas.height = Math.floor(viewport.height * outputScale);
            canvas.style.width = Math.floor(viewport.width) + "px";
            canvas.style.height = Math.floor(viewport.height) + "px";
            canvas.style.border = "2px solid black";

            let transform =
              outputScale !== 1 ? [outputScale, 0, 0, outputScale, 0, 0] : null;

            let renderContext = {
              canvasContext: context,
              transform: transform,
              viewport: viewport,
            };
            page.render(renderContext);
          });
        });

      </script>
    </body>
  </html>

  */
