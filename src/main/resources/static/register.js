const registerForm = document.getElementById("register-form");
const usernameInput = document.getElementById("register-username");
const passwordInput = document.getElementById("register-password");
const confirmInput = document.getElementById("register-confirm-password");
const registerMessage = document.getElementById("register-message");
const submitButton = registerForm.querySelector('button[type="submit"]');

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (submitButton.disabled) return;

    const userName = usernameInput.value.trim();
    const password = passwordInput.value;
    if (!userName) {
        registerMessage.dataset.state = "error";
        registerMessage.textContent = "Tên đăng nhập không được để trống.";
        usernameInput.focus();
        return;
    }
    if (password !== confirmInput.value) {
        registerMessage.dataset.state = "error";
        registerMessage.textContent = "Mật khẩu nhập lại chưa khớp.";
        confirmInput.focus();
        return;
    }

    submitButton.disabled = true;
    registerMessage.dataset.state = "pending";
    registerMessage.textContent = "Đang tạo tài khoản…";

    try {
        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userName, password })
        });

        if (response.status === 201) {
            registerMessage.dataset.state = "success";
            registerMessage.textContent = "Đăng ký thành công.";
            await new Promise(resolve => setTimeout(resolve, 1000));
            window.location.replace("/login.html");
            return;
        }

        if (response.status === 400 || response.status === 409) {
            const data = await response.json().catch(() => null);
            registerMessage.dataset.state = "error";
            registerMessage.textContent = data?.message ?? (response.status === 409
                ? "Tên đăng nhập đã được sử dụng."
                : "Thông tin đăng ký chưa hợp lệ.");
        } else {
            registerMessage.dataset.state = "error";
            registerMessage.textContent = "Chưa tạo được tài khoản. Bạn thử lại nhé.";
        }
    } catch {
        registerMessage.dataset.state = "error";
        registerMessage.textContent = "Không kết nối được máy chủ. Bạn thử lại nhé.";
    }

    submitButton.disabled = false;
});

submitButton.disabled = false;
