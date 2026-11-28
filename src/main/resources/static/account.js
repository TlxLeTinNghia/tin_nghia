(() => {
    const account = document.querySelector(".store-account");
    const adminLink = document.querySelector(".store-admin-link");
    const loginLink = account.querySelector(".store-account-login");
    const userName = account.querySelector(".store-account-name");
    const logoutButton = account.querySelector(".store-account-logout");
    const message = account.querySelector(".store-account-message");
    const isOrdersPage = account.hasAttribute("data-orders-page");

    function showAccount(name, role) {
        const signedIn = typeof name === "string" && name.length > 0;
        userName.textContent = signedIn ? name : "";
        userName.title = userName.textContent;
        userName.hidden = !signedIn;
        logoutButton.hidden = !signedIn;
        loginLink.hidden = signedIn;
        if (adminLink) adminLink.hidden = !signedIn || role !== "ADMIN";
    }

    async function refreshAccount() {
        try {
            const response = await fetch("/api/auth/me", { cache: "no-store" });
            if (response.status === 401) {
                showAccount();
                return;
            }
            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            const user = await response.json();
            showAccount(user.userName, user.role);
        } catch (error) {
            console.error("Không tải được thông tin tài khoản", error);
        }
    }

    logoutButton.addEventListener("click", async () => {
        logoutButton.disabled = true;
        message.textContent = "";

        try {
            const response = await fetch("/api/auth/logout", { method: "POST" });
            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            if (isOrdersPage) {
                window.location.replace("/");
                return;
            }
            showAccount();
            loginLink.focus();
        } catch (error) {
            message.textContent = "Chưa đăng xuất được. Bạn thử lại nhé.";
            console.error("Không đăng xuất được", error);
        } finally {
            logoutButton.disabled = false;
        }
    });

    // Đọc lại phiên nếu trình duyệt khôi phục một trang cũ bằng nút Quay lại.
    window.addEventListener("pageshow", event => {
        if (!event.persisted) return;
        if (isOrdersPage) {
            window.location.reload();
        } else {
            refreshAccount();
        }
    });

    refreshAccount();
})();
