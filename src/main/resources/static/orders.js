const ordersState = document.getElementById("orders-state");
const ordersMessage = document.getElementById("orders-message");
const ordersLogin = document.getElementById("orders-login");
const ordersShop = document.getElementById("orders-shop");
const ordersRetry = document.getElementById("orders-retry");
const ordersTable = document.getElementById("orders-table");
const ordersBody = document.getElementById("orders-body");
const orderDetailTemplate = document.getElementById("order-detail-template");
const formatCurrency = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

function showOrdersMessage(message, action) {
    ordersTable.hidden = true;
    ordersState.hidden = false;
    ordersMessage.textContent = message;
    ordersLogin.hidden = action !== "login";
    ordersShop.hidden = action !== "shop";
    ordersRetry.hidden = action !== "retry";
}

function formatAmount(amount) {
    return Number.isFinite(amount) ? formatCurrency.format(amount) : "—";
}

function createOrderItemRow(item) {
    const row = document.createElement("tr");
    const product = document.createElement("th");
    product.scope = "row";
    const productLabel = document.createElement("div");
    productLabel.className = "order-item-label";

    const image = document.createElement("img");
    image.alt = "";
    image.loading = "lazy";
    const defaultImage = "/Images/default-stationery.jpg";
    if (item.imageId == null) {
        image.src = defaultImage;
    } else {
        image.addEventListener("error", () => {
            image.src = defaultImage;
        }, { once: true });
        image.src = `/api/variants/${encodeURIComponent(item.imageId)}/image`;
    }

    const name = document.createElement("span");
    name.textContent = item.variantName ? `${item.productName} — ${item.variantName}` : item.productName;
    productLabel.append(image, name);
    product.append(productLabel);

    const unitPrice = document.createElement("td");
    unitPrice.textContent = formatAmount(item.unitPrice);
    const quantity = document.createElement("td");
    quantity.textContent = item.quantity;
    const total = document.createElement("td");
    if (Number.isFinite(item.originalAmount) && Number.isFinite(item.finalAmount) &&
        item.finalAmount < item.originalAmount) {
        const original = document.createElement("del");
        original.className = "order-item-original";
        original.textContent = formatAmount(item.originalAmount);
        total.append(original);
    }

    const final = document.createElement("strong");
    final.className = "order-amount";
    final.textContent = formatAmount(item.finalAmount);
    total.append(final);
    row.append(product, unitPrice, quantity, total);
    return row;
}

function renderOrderDetail(detailCell, orderDetail) {
    if (orderDetail.items.length === 0) {
        detailCell.textContent = "Đơn này chưa có món hàng.";
        return;
    }

    const content = orderDetailTemplate.content.cloneNode(true);
    content.querySelector("tbody").append(...orderDetail.items.map(createOrderItemRow));
    content.querySelector("tfoot td").textContent = formatAmount(orderDetail.totalAmount);
    detailCell.replaceChildren(content);
}

function createOrderDetailRow(orderId, toggle) {
    const row = document.createElement("tr");
    row.id = `order-details-${orderId}`;
    row.className = "order-detail-row";
    row.hidden = true;

    const cell = document.createElement("td");
    cell.colSpan = 3;
    cell.className = "order-detail-cell";
    row.append(cell);

    const message = document.createElement("p");
    message.className = "order-detail-message";
    message.setAttribute("role", "status");
    const retry = document.createElement("button");
    retry.type = "button";
    retry.className = "store-primary-link order-detail-retry";
    retry.textContent = "Tải lại chi tiết";

    let loading = false;
    let loaded = false;

    async function loadDetail() {
        if (loading || loaded) return;
        loading = true;
        message.textContent = "Đang tải chi tiết đơn…";
        cell.replaceChildren(message);

        try {
            const response = await fetch(`/api/orders/${encodeURIComponent(orderId)}`, { cache: "no-store" });
            if (response.status === 401) {
                showOrdersMessage("Phiên đăng nhập đã hết. Bạn đăng nhập lại để xem đơn mua nhé.", "login");
                ordersBody.replaceChildren();
                ordersLogin.focus();
                return;
            }
            if (response.status === 404) {
                message.textContent = "Không tìm thấy đơn mua này.";
                return;
            }
            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            renderOrderDetail(cell, await response.json());
            loaded = true;
        } catch (error) {
            message.textContent = "Chưa tải được chi tiết đơn. Bạn thử lại nhé.";
            cell.append(retry);
            console.error("Không tải được chi tiết đơn", error);
        } finally {
            loading = false;
        }
    }

    retry.addEventListener("click", () => {
        toggle.focus();
        loadDetail();
    });
    toggle.setAttribute("aria-controls", row.id);
    toggle.addEventListener("click", () => {
        const open = row.hidden;
        row.hidden = !open;
        toggle.setAttribute("aria-expanded", String(open));
        if (open) loadDetail();
    });
    return row;
}

function createOrderRows(order) {
    const row = document.createElement("tr");
    const number = document.createElement("th");
    number.scope = "row";
    number.className = "order-number";
    const toggle = document.createElement("button");
    toggle.type = "button";
    toggle.className = "order-toggle";
    toggle.setAttribute("aria-expanded", "false");
    toggle.setAttribute("aria-label", `Chi tiết đơn #${order.orderId}`);
    toggle.textContent = `#${order.orderId}`;
    const arrow = document.createElement("span");
    arrow.className = "order-toggle-arrow";
    arrow.setAttribute("aria-hidden", "true");
    toggle.append(arrow);
    number.append(toggle);

    const dateCell = document.createElement("td");
    dateCell.className = "order-date";
    // LocalDateTime từ API là giờ trên đồng hồ của cửa hàng.
    const dateParts = typeof order.orderDate === "string" &&
        /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2})(?:\.\d+)?)?$/.exec(order.orderDate);
    if (dateParts) {
        const [, year, month, day, hour, minute, second = "00"] = dateParts;
        const date = document.createElement("time");
        date.dateTime = order.orderDate;
        date.textContent = `${day}/${month}/${year} lúc ${hour}:${minute}:${second}`;
        dateCell.append(date);
    } else {
        dateCell.textContent = "—";
    }

    const amount = document.createElement("td");
    amount.className = "order-amount";
    amount.textContent = formatAmount(order.totalAmount);

    row.append(number, dateCell, amount);
    return [row, createOrderDetailRow(order.orderId, toggle)];
}

async function loadOrders() {
    showOrdersMessage("Đang tải đơn mua…");
    try {
        const response = await fetch("/api/orders", { cache: "no-store" });
        if (response.status === 401) {
            showOrdersMessage("Bạn cần đăng nhập để xem đơn mua.", "login");
            return;
        }
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const orders = await response.json();
        if (orders.length === 0) {
            showOrdersMessage("Bạn chưa có đơn mua nào.", "shop");
            return;
        }

        ordersBody.replaceChildren(...orders.flatMap(createOrderRows));
        ordersState.hidden = true;
        ordersTable.hidden = false;
    } catch (error) {
        showOrdersMessage("Chưa tải được đơn mua. Bạn thử lại nhé.", "retry");
        console.error("Không tải được đơn mua", error);
    }
}

ordersRetry.addEventListener("click", loadOrders);
loadOrders();
