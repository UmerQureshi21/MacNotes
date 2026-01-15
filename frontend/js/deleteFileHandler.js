export function createDeleteButton(filename, onSuccessCallback = null) {
  const deleteButton = document.createElement("button");
  deleteButton.setAttribute("data-filename", filename);
  deleteButton.innerHTML = `<span>Delete</span>`;

  deleteButton.addEventListener("click", function (event) {
    event.preventDefault();
  
    const filename = this.getAttribute("data-filename");
    console.log("Filename being sent:", filename);
  
    if (!confirm(`Are you sure you want to delete "${filename}"?`)) return;
  
    fetch("server/deleteFileFromMFiles.php", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `filename=${encodeURIComponent(filename)}`
    })
    .then(response => response.json())
    .then(data => {
      if (!data.success) throw new Error("mfiles deletion failed: " + data.message);

      return fetch("server/deleteFileFromDownloadedFiles.php", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `filename=${encodeURIComponent(filename)}`
      });
    })
    .then(response => response.json())
    .then(data => {
      if (!data.success) throw new Error("downloadedfiles deletion failed: " + data.message);

      return fetch("server/deleteFileFromRatings.php", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `filename=${encodeURIComponent(filename)}`
      });
    })
    .then(response => response.json())
    .then(data => {
      if (!data.success) throw new Error("ratings deletion failed: " + data.message);

      return fetch("server/deleteFileFromUploads.php", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `filename=${encodeURIComponent(filename)}`
      });
    })
    .then(response => response.json())
    .then(data => {
      if (!data.success) throw new Error("file deletion failed: " + data.message);

      alert("File fully deleted.");
      if (typeof onSuccessCallback === "function") {
        onSuccessCallback();
      }
    })
    .catch(error => {
      console.error("Deletion error:", error);
      alert(error.message);
    });
  });

  return deleteButton;
}
