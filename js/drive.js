window.addEventListener("DOMContentLoaded", () =>
{
	if(document.cookie.indexOf("passingID") !== -1 && document.querySelector("#needJump").innerText === "Hello world") window.location = location.href.replace(location.search, "");
});
