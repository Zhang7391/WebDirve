window.addEventListener("DOMContentLoaded", () =>
{
	if(document.querySelector("#show_zone").childNodes.length === 1)
		document.querySelector("#noticeMessage").hidden = false;

    document.querySelector(".failed_btn").addEventListener("click", error => 
	{
        document.querySelector(".upload_failed").hidden = true;
    }, false);

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
        if (!document.querySelector(".upload_failed").hidden) return;

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
        if (!document.querySelector(".upload_failed").hidden) return;
		document.querySelector("#upload").click();
		document.querySelector("#noticeMessage").hidden = true;

        for (let i = 0; files.length > i; i++) 
		{
            const file = files[i];

            let check = false;
            for (let j = 0, fileList = document.querySelectorAll("#filelist"); j < fileList.length; j++)
            {
                if (file["name"] === fileList[j].innerText){
                    check = true;
                    break;
                }
            }
            if (check)
            {
                check = false;
                failWindow("檔案重複");
                break;
            }

            const tr = document.createElement("tr");
            tr.classList.add("obj_tr");

            const td = document.createElement("td");
            td.innerText = file["name"];
            td.classList.add("obj_td");
            td.setAttribute("id", "filelist");

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

    function failWindow(text)
    {
        for (let i=0; i<document.querySelectorAll("#disposable").length; i++) 
		{
            document.querySelectorAll("#disposable")[i].remove();
        }

        let content = document.createElement("div");

        content.textContent = text;
        content.setAttribute("id", "disposable");

        document.querySelector(".upload_failed").appendChild(content);
        document.querySelector(".upload_failed").hidden = false;
    }

	// true is capturing
	// false is bubbling
    dropbox.addEventListener("dragenter", dragenter, false);
    dropbox.addEventListener("dragleave", dragleave, false);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);
});
