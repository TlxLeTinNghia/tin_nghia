const loginForm = document.getElementById("login-form");
const usernameInput = document.getElementById("login-username");
const passwordInput = document.getElementById("login-password");
const loginMessage = document.getElementById("login-message");
const submitButton = loginForm.querySelector('button[type="submit"]');

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (submitButton.disabled) return;

    const userName = usernameInput.value.trim();
    if (!userName) {
        loginMessage.dataset.state = "error";
        loginMessage.textContent = "Bạn hãy nhập tên đăng nhập.";
        usernameInput.focus();
        return;
    }

    submitButton.disabled = true;
    loginMessage.dataset.state = "pending";
    loginMessage.textContent = "Đang đăng nhập…";

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify({ userName, password: passwordInput.value })
        });

        if (response.ok) {
            loginMessage.dataset.state = "success";
            loginMessage.textContent = "Đăng nhập thành công.";
            await new Promise(resolve => setTimeout(resolve, 1000));
            const next = new URLSearchParams(window.location.search).get("next");
            window.location.replace(["/orders.html", "/admin.html"].includes(next) ? next : "/");
            return;
        }

        if (response.status === 401) {
            const data = await response.json().catch(() => null);
            loginMessage.dataset.state = "error";
            loginMessage.textContent = data?.message ?? "Tên đăng nhập hoặc mật khẩu không đúng.";
            passwordInput.focus();
            passwordInput.select();
        } else {
            loginMessage.dataset.state = "error";
            loginMessage.textContent = "Chưa đăng nhập được. Bạn thử lại nhé.";
        }
    } catch (error) {
        loginMessage.dataset.state = "error";
        loginMessage.textContent = "Không kết nối được máy chủ. Bạn thử lại nhé.";
    } finally {
        submitButton.disabled = false;
    }
});
