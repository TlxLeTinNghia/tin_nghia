for (const button of document.querySelectorAll(".password-toggle")) {
    const input = document.getElementById(button.getAttribute("aria-controls"));

    button.addEventListener("click", () => {
        const showPassword = input.type === "password";
        input.type = showPassword ? "text" : "password";
        button.classList.toggle("is-revealed", showPassword);

        const label = input.labels[0].textContent.trim().toLowerCase();
        button.setAttribute("aria-label", (showPassword ? "Ẩn " : "Xem ") + label);
    });
}
