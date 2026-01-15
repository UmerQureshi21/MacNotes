/**
 * This is the js file of the upload page and the file upload feature
 * Authors: Sepanta Kamali & Alhan Walli
 */
window.addEventListener("load", function(event){
    // Set DOM elements
    let fileTitleInput = document.getElementById("titleInput");
    let courseCodeInput = document.getElementById("courseInput");
    let descriptionInput = document.getElementById("descriptionInput");

    // Handle upload button click
    document.getElementById("uploadButton").addEventListener("click", function (event) {
        event.preventDefault(); //prevent default behaviour
        
        //Get parameters
        let fileInput = document.getElementById("file");
        let file = fileInput.files[0];

        let filetitle = fileTitleInput.value.trim();
        let coursecode = courseCodeInput.value.trim();
        let description = descriptionInput.value.trim();
        
        //If file is not selected, throw an error
        if (!file) {
            showMessage("Please select a file to upload.", "error");
            return;
        }
        // Prepare form data to send to server
        let formData = new FormData();
        formData.append("file", file);
        formData.append("filetitle", filetitle);
        formData.append("coursecode", coursecode);
        formData.append("description", description);

        // Send the POST request to upload the file
        fetch("./server/upload.php", {
            method: "POST",
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            // If upload succeeds
            if (data.success) {
                showMessage(data.message, "success");
                // Clear form fields after successful upload
                fileInput.value = "";
                fileTitleInput.value = "";
                courseCodeInput.value = "";
                descriptionInput.value = "";
            } else {
                // If upload fails, throw error
                showMessage(data.message, "error");
            }
        })
        .catch(error => {
            //catch errors
            showMessage("Something went wrong. Try again!", "error");
        });
    });

    /**
     * Shows a success or error message to the user.
     *
     * @param {string} message | The message text to display.
     * @param {string} type | The type of message ("success" or "error") to determine color.
     * @return {void}
     */
    function showMessage(message, type) {
        let messageBox = document.getElementById("uploadMessage");
        messageBox.textContent = message;
        messageBox.style.color = type === "success" ? "green" : "red";
        messageBox.style.display = "block";
    }

    //Limit file title input to 199 characters
    fileTitleInput.addEventListener("input", function(event){
        let currentValue = this.value;

        if (currentValue.length > 199) {
            this.value = currentValue.slice(0, 199);
        }

        if (currentValue.length === 199) {
            this.maxLength = 199; 
        }
    });
    
    // Limit course code input to 5 characters
    courseCodeInput.addEventListener("input", function (event) {
        let currentValue = this.value;

        if (currentValue.length > 5) {
            this.value = currentValue.slice(0, 5);
        }

        if (currentValue.length === 5) {
            this.maxLength = 5; 
        }
    });
    
    // Limit description input to 199 characters
    descriptionInput.addEventListener("input", function (event) {
        let currentValue = this.value;

        if (currentValue.length > 199) {
            this.value = currentValue.slice(0, 199);
        }

        if (currentValue.length === 199) {
            this.maxLength = 199; 
        }
    });

});
