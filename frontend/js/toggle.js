/**
 * This is the js file of the dark mode feature
 * Author: Umer Qureshi
 */
window.addEventListener("load", function () {
  let modeContainer = document.querySelector(".mode");
  let nodeSwitch = document.querySelector(".toggle-switch");
  let text = document.querySelector(".mode-text");

  // Set default value if not already set
  if (!sessionStorage.getItem("isDark")) {
    sessionStorage.setItem("isDark", "Yes");
    console.log("First time: Dark mode");
  }

  // Apply the theme based on current setting
  if (sessionStorage.getItem("isDark") === "Yes") {
    applyDarkMode();
    modeContainer.classList.add("dark");
  } else {
    applyLightMode();
    modeContainer.classList.remove("dark");
  }

  // On click, toggle theme
  nodeSwitch.addEventListener("click", function () {
    const isCurrentlyDark = sessionStorage.getItem("isDark") === "Yes";
    if (isCurrentlyDark) {
      applyLightMode();
      modeContainer.classList.remove("dark");
      sessionStorage.setItem("isDark", "No");
    } else {
      applyDarkMode();
      modeContainer.classList.add("dark");
      sessionStorage.setItem("isDark", "Yes");
    }
  });

  // Helper functions
  function applyDarkMode() {
    document.documentElement.style.setProperty("--text-color", "#b1b1b1");
    document.documentElement.style.setProperty("--sidebar-color", "#2b2b2b");
    document.documentElement.style.setProperty(
      "--lighter-background-color",
      "#434343"
    );
    document.documentElement.style.setProperty(
      "--stronger-background-color",
      "rgb(26, 26, 26)"
    );
    document.querySelector(".sun").style.opacity = 1;
    document.querySelector(".moon").style.opacity = 0;
    text.innerHTML = "Dark Mode";
  }
  function applyLightMode() {
    document.documentElement.style.setProperty("--text-color", "#7a003c");
    document.documentElement.style.setProperty("--sidebar-color", "#fdbf57");
    document.documentElement.style.setProperty(
      "--lighter-background-color",
      "#cccccc"
    );
    document.documentElement.style.setProperty(
      "--stronger-background-color",
      "white"
    );
    document.querySelector(".sun").style.opacity = 0;
    document.querySelector(".moon").style.opacity = 1;
    text.innerHTML = "Light Mode";
  }
});
