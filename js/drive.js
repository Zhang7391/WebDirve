window.addEventListener("DOMContentLoaded", () =>
{
	if(document.querySelector("#show_zone").childNodes.length === 1)
		document.querySelector("#noticeMessage").hidden = false;

    const dropbox = document.querySelector("#upload_zone");
    const dropbox_effect = document.querySelector("#drag_effect");

	document.querySelector("#fileUploader").addEventListener("change", (events) =>
	{
		let files = events.target.files;

		handleFiles(files);
	});

    let firstenter = null;

    function dragenter(e) 
	{
        firstenter = e.target;
        dropbox_effect.classList.add("upload_zone_enter");

        e.stopPropagation();
        e.preventDefault();
    }

    function dragleave(e) 
	{
        if (e.target === firstenter)
		{
            dropbox_effect.classList.remove("upload_zone_enter");
        }
    }

    function dragover(e) 
	{
        e.stopPropagation();
        e.preventDefault();
    }

	function handleFiles(files)
	{
		document.querySelector("#noticeMessage").hidden = true;

        for (let i = 0; files.length > i; i++) 
		{
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
            document.querySelector("#show_zone").appendChild(tr);
        }
    };

    function drop(e) 
	{
        e.stopPropagation();
        e.preventDefault();
        dropbox_effect.classList.remove("upload_zone_enter");

        const dt = e.dataTransfer;
        const files = dt.files;

        handleFiles(files);
    }

	// true is capturing
	// false is bubbling
    dropbox.addEventListener("dragenter", dragenter, false);
    dropbox.addEventListener("dragleave", dragleave, false);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);
});
