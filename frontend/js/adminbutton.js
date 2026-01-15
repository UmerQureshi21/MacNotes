window.addEventListener("DOMContentLoaded", function (event) {
    // Check if the user is an admin before doing anything else
    fetch("server/checkAdmin.php")
    .then(response => response.json())
    .then(data => {
        let sidebar = document.querySelector(".sidebar-menu");
        if (data.is_admin) {
            //remove dashboard and myfiles button
            let dashboardButton = document.querySelector(".dashboard-button");
            let myFilesButton = document.querySelector(".myfiles-button");

            dashboardButton.remove();
            myFilesButton.remove();

            //add admin button
            let logoutbutton = document.querySelector(".logout-button");
            let option = document.createElement("li")
            option.classList.add("admin-button");
            option.innerHTML = `<a href="admin.html">
                <i class="ri-admin-line"></i>
                <span id="sidebar-options">Admin</span>
                </a>`;
            sidebar.insertBefore(option, logoutbutton);
        }
        sidebar.style.visibility = "visible"; 
        
    })
    .catch(error => {
        console.error("Error checking admin:", error);
        window.location.href = "login.html";
      });
});


