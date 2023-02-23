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

    function dragenter(evented) 
	{
        firstenter = evented.target;
        dropbox_effect.classList.add("upload_zone_enter");

        evented.stopPropagation();
        evented.preventDefault();
    }

    function dragleave(evented) 
	{
        if(evented.target === firstenter)
		{
            dropbox_effect.classList.remove("upload_zone_enter");
        }
    }

    function dragover(evented) 
	{
        evented.stopPropagation();
        evented.preventDefault();
    }

	function handleFiles(files)
	{
		document.querySelector("#upload").click();
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

    function drop(evented) 
	{
        evented.preventDefault();
        dropbox_effect.classList.remove("upload_zone_enter");

		let files = evented.dataTransfer.files;

		let list = new DataTransfer();
		for(let x of files) list.items.add(x);
		document.querySelector("#fileUploader").files = list.files;

        handleFiles(files);
    }

	// true is capturing
	// false is bubbling
    dropbox.addEventListener("dragenter", dragenter, false);
    dropbox.addEventListener("dragleave", dragleave, false);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);
});
