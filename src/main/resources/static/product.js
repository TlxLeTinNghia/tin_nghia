const productId = new URLSearchParams(window.location.search).get("id");
const defaultImage = "/Images/default-stationery.jpg";
const galleryInterval = 2000;
const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)");
const formatPrice = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });
const formatPercent = new Intl.NumberFormat("vi-VN", { style: "percent", maximumFractionDigits: 2 });

const detail = document.getElementById("product-detail");
const productStatus = document.getElementById("product-status");
const mainImage = document.getElementById("product-main-image");
const thumbnails = document.getElementById("product-thumbnails");
const autoplayButton = document.getElementById("gallery-autoplay");
const variantList = document.getElementById("product-variants");
const variantStatus = document.getElementById("variant-status");
const minusButton = document.getElementById("quantity-minus");
const plusButton = document.getElementById("quantity-plus");
const quantityValue = document.getElementById("quantity-value");
const originalPrice = document.getElementById("product-original-price");
const discount = document.getElementById("product-discount");
const unitPrice = document.getElementById("product-unit-price");
const totalPrice = document.getElementById("product-total");
const buyNow = document.getElementById("buy-now");
const orderStatus = document.getElementById("order-status");

let activeImage = 0;
let galleryTimer;
let autoplay = !reducedMotion.matches;
let quantity = 1;
let selectedVariant = null;
let placingOrder = false;

function useDefaultOnError(image) {
    image.addEventListener("error", () => {
        if (image.getAttribute("src") !== defaultImage) image.src = defaultImage;
    });
}

function showGalleryImage(index) {
    thumbnails.children[activeImage]?.firstElementChild.setAttribute("aria-pressed", "false");
    activeImage = index;
    const item = thumbnails.children[index];
    const button = item.firstElementChild;
    button.setAttribute("aria-pressed", "true");
    mainImage.src = button.firstElementChild.getAttribute("src");
    mainImage.alt = button.getAttribute("aria-label");

    const strip = thumbnails.getBoundingClientRect();
    const selected = item.getBoundingClientRect();
    if (selected.right > strip.right) thumbnails.scrollLeft += selected.right - strip.right + 4;
    if (selected.left < strip.left) thumbnails.scrollLeft -= strip.left - selected.left + 4;
}

function scheduleGalleryRotation() {
    clearInterval(galleryTimer);
    autoplayButton.textContent = autoplay ? "Tạm dừng trình chiếu" : "Phát trình chiếu";
    if (autoplay && thumbnails.children.length > 1 && !document.hidden) {
        galleryTimer = setInterval(() => {
            showGalleryImage((activeImage + 1) % thumbnails.children.length);
        }, galleryInterval);
    }
}

function renderGallery(product) {
    const imageUrls = new Set();
    if (product.hasImage) imageUrls.add(`/api/products/${encodeURIComponent(product.productId)}/image`);
    for (const imageId of product.imageIds ?? []) {
        if (imageId != null) imageUrls.add(`/api/variants/${encodeURIComponent(imageId)}/image`);
    }
    if (!imageUrls.size) imageUrls.add(defaultImage);

    for (const url of imageUrls) {
        const index = thumbnails.children.length;
        const item = document.createElement("li");
        const button = document.createElement("button");
        button.type = "button";
        button.className = "product-thumbnail";
        button.setAttribute("aria-label", `Ảnh ${index + 1} của ${product.productName}`);
        button.setAttribute("aria-pressed", "false");
        const image = document.createElement("img");
        image.alt = "";
        useDefaultOnError(image);
        image.src = url;
        button.append(image);
        item.append(button);
        thumbnails.append(item);

        button.addEventListener("click", () => {
            showGalleryImage(index);
            scheduleGalleryRotation();
        });
    }

    showGalleryImage(0);
    autoplayButton.hidden = thumbnails.children.length < 2;
    scheduleGalleryRotation();
}

function updatePrice() {
    quantityValue.textContent = quantity;
    minusButton.disabled = placingOrder || !selectedVariant || quantity <= 1;
    plusButton.disabled = placingOrder || !selectedVariant || quantity >= selectedVariant.unitsInStock;
    buyNow.disabled = placingOrder || !selectedVariant;
    if (!selectedVariant) return;

    const finalPrice = selectedVariant.unitPrice * (1 - selectedVariant.discount);
    originalPrice.hidden = !selectedVariant.discount;
    originalPrice.textContent = formatPrice.format(selectedVariant.unitPrice);
    discount.hidden = !selectedVariant.discount;
    discount.textContent = `−${formatPercent.format(selectedVariant.discount)}`;
    unitPrice.textContent = formatPrice.format(finalPrice);
    totalPrice.hidden = false;
    totalPrice.textContent = `Tạm tính ${formatPrice.format(finalPrice * quantity)}`;
}

async function loadVariantThumbnail(image, variantId) {
    try {
        const response = await fetch(`/api/variants/${encodeURIComponent(variantId)}/images`);
        if (!response.ok) return;
        const images = await response.json();
        const imageId = images?.[0]?.imageId;
        if (imageId != null) image.src = `/api/variants/${encodeURIComponent(imageId)}/image`;
    } catch (error) {
        console.error("Không tải được ảnh biến thể", error);
    }
}

function renderVariants(variants) {
    const available = variants.some(variant => variant.unitsInStock > 0);
    variantStatus.hidden = available;
    variantStatus.textContent = variants.length ? "Các phân loại hiện đã hết hàng." : "Chưa có phân loại.";
    for (const variant of variants) {
        const item = document.createElement("li");
        const button = document.createElement("button");
        button.type = "button";
        button.className = "product-variant-button";
        button.disabled = variant.unitsInStock <= 0;
        button.setAttribute("aria-pressed", "false");

        const image = document.createElement("img");
        image.alt = "";
        useDefaultOnError(image);
        image.src = defaultImage;
        const name = document.createElement("span");
        name.textContent = variant.variantName;
        const stock = document.createElement("span");
        stock.className = "product-variant-stock";
        stock.textContent = variant.unitsInStock > 0 ? `Còn ${variant.unitsInStock}` : "Hết hàng";

        button.addEventListener("click", () => {
            if (placingOrder) return;
            variantList.querySelector('[aria-pressed="true"]')?.setAttribute("aria-pressed", "false");
            button.setAttribute("aria-pressed", "true");
            selectedVariant = variant;
            quantity = 1;
            orderStatus.hidden = true;
            updatePrice();
        });

        button.append(image, name, stock);
        item.append(button);
        variantList.append(item);
        loadVariantThumbnail(image, variant.variantId);
    }
}

async function loadProductDetail() {
    if (!productId) {
        productStatus.textContent = "Thiếu mã sản phẩm.";
        return;
    }

    try {
        const response = await fetch(`/api/products/${encodeURIComponent(productId)}`);
        if (response.status === 404) {
            productStatus.textContent = "Không tìm thấy sản phẩm.";
            return;
        }
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const product = await response.json();
        document.title = `${product.productName} | StationeryStore`;
        document.getElementById("product-name").textContent = product.productName;
        document.getElementById("product-description").textContent = product.description;
        document.getElementById("product-rating").textContent = product.averageRating == null || product.reviewCount === 0
            ? "Chưa có đánh giá"
            : `${product.averageRating}/5 · ${product.reviewCount} lượt đánh giá`;
        detail.hidden = false;
        renderGallery(product);
        renderVariants(product.variants ?? []);
        updatePrice();
        productStatus.hidden = true;
    } catch (error) {
        detail.hidden = true;
        productStatus.textContent = "Không tải được sản phẩm. Bạn thử tải lại trang nhé.";
        console.error(error);
    }
}

autoplayButton.addEventListener("click", () => {
    autoplay = !autoplay;
    scheduleGalleryRotation();
});
document.addEventListener("visibilitychange", scheduleGalleryRotation);
reducedMotion.addEventListener("change", event => {
    autoplay = !event.matches;
    scheduleGalleryRotation();
});
useDefaultOnError(mainImage);

minusButton.addEventListener("click", () => {
    quantity--;
    updatePrice();
});
plusButton.addEventListener("click", () => {
    quantity++;
    updatePrice();
});

buyNow.addEventListener("click", async () => {
    if (!selectedVariant || placingOrder) return;

    placingOrder = true;
    orderStatus.hidden = true;
    updatePrice();

    try {
        const response = await fetch("/api/orders", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variantId: selectedVariant.variantId, quantity })
        });
        const data = await response.json().catch(() => ({}));
        if (!response.ok) {
            orderStatus.textContent = data?.message ?? "Không tạo được đơn hàng.";
        } else {
            orderStatus.textContent = data?.orderId ? `Đã đặt đơn #${data.orderId}.` : "Đã đặt hàng.";
            const ordersLink = document.createElement("a");
            ordersLink.href = "/orders.html";
            ordersLink.textContent = "Xem đơn mua";
            orderStatus.append(" ", ordersLink);
        }
    } catch (error) {
        console.error(error);
        orderStatus.textContent = "Không nhận được phản hồi. Hãy kiểm tra đơn trước khi thử lại.";
    } finally {
        orderStatus.hidden = false;
        placingOrder = false;
        updatePrice();
    }
});

loadProductDetail();
