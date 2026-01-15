/**
 * This is the js file of the login page
 * Author: Marko Kosoric
 */
window.addEventListener("load", function(event) {
    // Set DOM elements
    const signInResultContainer = document.getElementById("signin-result");


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

        fetch("./server/adminSignIn.php", config)
            .then(response => response.json())
            .then(data => {
                // Handle the response here
                signInResultContainer.innerHTML = "<p>" + data.message + "</p>";
                signInResultContainer.style.color = data.status === "success" ? "green" : "red";
                console.log("SIGN IN RESPONSE:", data);

                if (data.status === "success") {
                    // If signin is successful, redirect to dashboard
                    window.location.href = "admin.html";
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
