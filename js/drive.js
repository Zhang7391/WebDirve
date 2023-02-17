window.addEventListener("DOMContentLoaded", () =>
{
//	if(document.cookie.indexOf("passingID") !== -1) window.location = location.href.replace(location.search, "");
    const dropbox = document.getElementById("upload_zone");
    const dropbox_effect = document.getElementById("drag_effect");
    let firstenter = null;

    function dragenter(e) {
        firstenter = e.target;
        dropbox_effect.classList.add("upload_zone_enter");
        e.stopPropagation();
        e.preventDefault();
    }

    function dragleave (e) {
        if (e.target === firstenter){
            dropbox_effect.classList.remove("upload_zone_enter");
        }
    }

    function dragover(e) {
        e.stopPropagation();
        e.preventDefault();
    }

    function handleFiles(files) {
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const tr = document.createElement("tr");
            tr.classList.add("obj_tr");

            const td = document.createElement("td");
            td.innerText = file["name"];
            td.classList.add("obj_td");

            const td2 = document.createElement("td");
            td2.innerText = "-";
            td2.classList.add("obj_td");

            const td3 = document.createElement("td");
            td3.innerText = "-";
            td3.classList.add("obj_td");

            tr.appendChild(td)
            tr.appendChild(td2)
            tr.appendChild(td3)
            show_zone.appendChild(tr);
        }
    }

    function drop(e) {
        e.stopPropagation();
        e.preventDefault();
        dropbox_effect.classList.remove("upload_zone_enter");

        const dt = e.dataTransfer;
        const files = dt.files;
        handleFiles(files);
    }

    dropbox.addEventListener("dragenter", dragenter, false);
    dropbox.addEventListener("dragleave", dragleave, false);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);

});
