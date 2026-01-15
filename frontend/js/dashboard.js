/**
 * This is the js file of the dashboard page
 * Authors: Sepanta Kamali & Alhan Walli & Aryan Minhas
 */
window.addEventListener("load", function (event) {
  // CAROSEL CODE
  //Set DOM elements and variables
  let slidesContainer = document.querySelector(".multi-item-carousel .slides");
  let slideItems = document.querySelectorAll(".multi-item-carousel .slide");
  let totalSlides = slideItems.length;
  let visibleSlides = 3;
  let maxIndex = totalSlides - visibleSlides;
  let currentIndex = 0;

  // Set the width of the slides container
  function updateCarousel() {
    // Move the container by the width of one slide (which is 100/3% each time)
    let offset = -currentIndex * (100 / visibleSlides);
    slidesContainer.style.transform = `translateX(${offset}%)`;
  }

  // Next button
  document
    .querySelector(".multi-item-carousel .next")
    .addEventListener("click", () => {
      currentIndex++;
      if (currentIndex > maxIndex) {
        currentIndex = 0;
      }
      updateCarousel();
    });

  // Prev button
  document
    .querySelector(".multi-item-carousel .prev")
    .addEventListener("click", () => {
      currentIndex--;
      if (currentIndex < 0) {
        currentIndex = maxIndex;
      }
      updateCarousel();
    });

  // Auto-play
  setInterval(() => {
    currentIndex = (currentIndex + 1) % (maxIndex + 1);
    updateCarousel();
  }, 4000);

  // DROPDOWN PANEL CODE
  // Set DOM Elements
  const toggleButtons = document.querySelectorAll(
    ".dropdown-band .toggle-panel"
  );
  // Set events for toggle buttons
  toggleButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.stopPropagation(); // Prevent the click event from bubbling up
      let target = button.getAttribute("data-target");
      let panel = document.querySelector(`.dropdown-panel.${target}`);
      panel.classList.toggle("open");
    });
  });
  //Close both panels when clicked outside
  document.addEventListener("click", function (e) {
    if (
      !e.target.closest(".dropdown-band") &&
      !e.target.closest(".dropdown-panels")
    ) {
      document.querySelectorAll(".dropdown-panel").forEach((panel) => {
        panel.classList.remove("open");
      });
    }
  });

  //set user welcome header
  let userWelcomeHeader = document.getElementById("user-welcome-header");
  // Fetch user data to set welcome message
  fetch("./server/getUser.php")
    .then((response) => response.json())
    .then((data) => {
      if (data.access) {
        let username = data.username;
        userWelcomeHeader.innerHTML = `Hello, ${username}`;
      }
    });
  
  //set User Dasboard Stats
  //Initialize variables
  let userTotalUploads = document.getElementById("user-total-uploads");
  let userTotalDownloads = document.getElementById("user-total-downloads");
  let userAverageRatings = document.getElementById("user-total-ratings");

  // Update user dashboard stats every 100ms
  setInterval(function () {
    fetch("./server/dashboard.php")
      .then((response) => response.json())
      .then((data) => {
        userTotalUploads.innerHTML = data.numberOfUploads;
        userTotalDownloads.innerHTML = data.numberOfDownloads;
        userAverageRatings.innerHTML = data.userAverageRating;
      })
      .catch((error) => {
        console.error("Error fetching dashboard data:", error);
      });
  }, 100);

  //Set Trending Files Section
  //Fetch trending files and display them every 100ms
  setInterval(function () {
    fetch("server/trending.php")
      .then((response) => response.json())
      .then((data) => {
        for (let i = 0; i < data.length; i++) {
          let fileTitleBox = document.getElementById("filetitle" + (i + 1));
          let courseCodeBox = document.getElementById("coursecode" + (i + 1));
          let viewBtn = document.getElementById("view-btn" + (i + 1));

          // Update trending file title and course code
          fileTitleBox.innerHTML = data[i].filetitle;
          courseCodeBox.innerHTML = data[i].coursecode;

          const file = data[i];
          // Set click event on view button to redirect with file details
          viewBtn.addEventListener("click", function () {
            const a = document.createElement("a");

            a.setAttribute(
              "href",
              `searchfiledetails.html?filename=${encodeURIComponent(
                file.filename
              )}&filetitle=${encodeURIComponent(
                file.filetitle
              )}&filedescription=${encodeURIComponent(
                file.description
              )}&coursecode=${encodeURIComponent(file.coursecode)}`
            );

            a.setAttribute("target", "");
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
          });
        }
      }) //catch error
      .catch((err) => console.error("Error fetching trending files:", err));
  }, 100);
});
