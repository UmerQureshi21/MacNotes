/**
 * This is the js file of the login page
 * Author: Sepanta Kamali
 */
window.addEventListener("load", function(event) {
    // Set DOM elements
    const container = document.getElementById("container");
    const overlayBtnSignUp = document.getElementById("overlay-btn-signup");
    const overlayBtnSignIn = document.getElementById("overlay-btn-signin");
    const companyName = document.getElementById("title-container");
    const signUpResultContainer = document.getElementById("signup-result");
    const signInResultContainer = document.getElementById("signin-result");

    // Overlay button (Switch Panels)
    overlayBtnSignUp.addEventListener("click", () => {
        container.classList.toggle("right-panel-active");
        companyName.classList.toggle("title-primary");
        companyName.classList.toggle("title-white");
    });

    overlayBtnSignIn.addEventListener("click", () => {
        container.classList.toggle("right-panel-active");
        companyName.classList.toggle("title-primary");
        companyName.classList.toggle("title-white");
    });

    // Sign up form
    let signUpEmailEntryBox = document.getElementById("signup-email");
    let signUpNameEntryBox = document.getElementById("signup-username");
    let signUpPasswordEntryBox = document.getElementById("signup-password");
    let signUpBtn = document.getElementById("signup-btn");

    // Sign up button click event listener
    signUpBtn.addEventListener("click", function(event) {
        event.preventDefault();

        // Collect and validate input fields
        let username = signUpNameEntryBox.value.trim();
        let userEmail = signUpEmailEntryBox.value.trim();
        let userPassword = signUpPasswordEntryBox.value.trim();

        //Chek if paramters are valid, if not throw error
        if (username === "" || userEmail === "" || userPassword === "") {
            signUpResultContainer.textContent = "All fields are required!";
            signUpResultContainer.style.color = "red";
            return;
        }

        // Prepare request data
        let params = "username=" + username + "&email=" + userEmail + "&password=" + userPassword;
        let config = {
            method: 'POST',
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        };

        // Send signup request to server
        fetch("./server/signup.php", config)
            .then(response => response.json())
            .then(data => {

                // check if the password is weak
                if (data.status == "InvPass") {
                    signUpResultContainer.innerHTML = "<p>" + data.message + "</p>";
                    signUpResultContainer.style.color = "red";
                    return;
                }
                // if the password is strong
                else{
                    // Handle the response here
                    signUpResultContainer.innerHTML = "<p>" + data.message + "</p>";
                    signUpResultContainer.style.color = data.status === "success" ? "green" : "red";

                    if (data.status === "success") {
                        // If signup is successful, redirect to dashboard
                        window.location.href = "dashboard.html";
                    }
                }
            })
            .catch(error => {
                // Catch errors
                signUpResultContainer.innerHTML = "An error occurred. Please try again.";
                signUpResultContainer.style.color = "red";
                console.error("Error:", error);
            });
    });

    // Sign in form
    let signInEmailEntryBox = document.getElementById("signin-email");
    let signInPasswordEntryBox = document.getElementById("signin-password");
    let signInBtn = document.getElementById("signin-btn");

    // Sign in button click
    signInBtn.addEventListener("click", function(event){
        event.preventDefault();
        // Collect and validate input fields
        let userEmail = signInEmailEntryBox.value.trim();
        let userPassword = signInPasswordEntryBox.value.trim();

        if (userEmail === "" || userPassword === "") {
            signInResultContainer.textContent = "All fields are required!";
            signInResultContainer.style.color = "red";
            return;
        }
        
        let params = "email=" + userEmail + "&password=" + userPassword;
        let config = {
            method: 'POST',
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        };

        fetch("./server/signin.php", config)
            .then(response => response.json())
            .then(data => {
                // Handle the response here
                signInResultContainer.innerHTML = "<p>" + data.message + "</p>";
                signInResultContainer.style.color = data.status === "success" ? "green" : "red";
                console.log("SIGN IN RESPONSE:", data);

                if (data.status === "success") {
                    // If signin is successful, redirect to dashboard
                    window.location.href = "dashboard.html";
                }
            })
            .catch(error => {
                //catch error
                signInResultContainer.innerHTML = "An error occurred. Please try again.";
                signInResultContainer.style.color = "red";
                console.error("Error:", error);
            });
    });
});
