/**
 * This is the js file of the report file page
 * Author: Sepanta Kamali
 */
window.addEventListener("load", function(event){
    //read url params
    const params = new URLSearchParams(window.location.search);
    const filename = params.get("filename");
    const filetitle = params.get("filetitle");
    const coursecode = params.get("coursecode");
    const macID = params.get("macID");

    //set DOM Elements
    const fileTitleBox = document.getElementById("filetitle");
    const courseCodeBox = document.getElementById("coursecode");
    const usernameBox = this.document.getElementById("username");

    //Fetch username based on filename
    let formData = new FormData();
    formData.append("filename", filename);

    fetch("server/getUserFromFileName.php", {
        method: "POST",
        body: formData,
    })
        .then(response => response.json())
        .then(data => {
            fileTitleBox.value = filetitle;
            courseCodeBox.value = coursecode;
            usernameBox.value = data.user_name;
        })
        //catch errors
        .catch((error) => console.error("Error:", error));
});
