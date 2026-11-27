const bannerImages = document.querySelectorAll(".store-hero-media img");
const searchForm = document.getElementById("search-form");
const searchInput = document.getElementById("search-input");
const suggestionList = document.getElementById("suggestions");
const productGrid = document.getElementById("product-grid");
const productStatus = document.getElementById("product-status");
const pagination = document.querySelector(".store-pagination");
const previousPageButton = document.getElementById("previous-page");
const nextPageButton = document.getElementById("next-page");
const pageInfo = document.getElementById("page-info");

const defaultImageUrl = "/Images/default-stationery.jpg";
const productPageSize = 20;
const ratingFormat = new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 1 });
let currentProductPage = 0;
let searchKeyword = (new URLSearchParams(window.location.search).get("keyword") ?? "").trim();
let productRequest;
let suggestionRequest;
let suggestionTimer;

if (bannerImages.length > 1 && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    let currentBanner = 0;
    setInterval(() => {
        bannerImages[currentBanner].hidden = true;
        currentBanner = (currentBanner + 1) % bannerImages.length;
        bannerImages[currentBanner].hidden = false;
    }, 3000);
}

function setImageSource(image, imageUrl) {
    if (imageUrl) {
        image.addEventListener("error", () => {
            image.src = defaultImageUrl;
        }, { once: true });
    }
    image.src = imageUrl || defaultImageUrl;
}

for (const image of bannerImages) {
    if (image.complete && image.naturalWidth === 0) {
        image.src = defaultImageUrl;
    } else {
        image.addEventListener("error", () => { image.src = defaultImageUrl; }, { once: true });
    }
}

function createProductCard(product) {
    const card = document.createElement("article");
    card.className = "store-product-card";

    const link = document.createElement("a");
    link.className = "store-product-link";
    link.href = `/product.html?id=${encodeURIComponent(product.productId)}`;

    const media = document.createElement("div");
    media.className = "store-product-media";
    const image = document.createElement("img");
    image.alt = "";
    image.loading = "lazy";
    const imageUrl = product.hasImage ? `/api/products/${encodeURIComponent(product.productId)}/image` : null;
    setImageSource(image, imageUrl);
    media.append(image);

    const copy = document.createElement("div");
    copy.className = "store-product-copy";

    const name = document.createElement("h3");
    name.textContent = product.productName;

    const description = document.createElement("p");
    description.textContent = product.description;

    const rating = document.createElement("p");
    rating.className = "store-product-rating";
    rating.textContent = product.averageRating == null || product.reviewCount === 0
        ? "Chưa có đánh giá"
        : `${ratingFormat.format(product.averageRating)}/5 · ${product.reviewCount} lượt đánh giá`;

    copy.append(name, description, rating);
    link.append(media, copy);
    card.append(link);
    return card;
}

async function loadProductPage(page) {
    productRequest?.abort();
    const request = new AbortController();
    productRequest = request;
    productGrid.setAttribute("aria-busy", "true");
    productGrid.replaceChildren();
    pagination.hidden = true;
    productStatus.textContent = "Đang tải sản phẩm…";
    productStatus.hidden = false;

    try {
        const params = new URLSearchParams({ page, size: productPageSize, keyword: searchKeyword });
        const response = await fetch(`/api/products?${params}`, { signal: request.signal });
        if (!response.ok) {
            throw new Error(`Không tải được sản phẩm (HTTP ${response.status})`);
        }

        const data = await response.json();
        if (request.signal.aborted) return;

        productGrid.replaceChildren(...data.content.map(createProductCard));
        currentProductPage = data.page;
        productStatus.textContent = "Không tìm thấy sản phẩm.";
        productStatus.hidden = data.content.length > 0;
        previousPageButton.disabled = currentProductPage === 0;
        nextPageButton.disabled = currentProductPage + 1 >= data.totalPages;
        pageInfo.textContent = `Trang ${currentProductPage + 1} / ${data.totalPages}`;
        pagination.hidden = data.totalPages < 2;
    } catch (error) {
        if (request.signal.aborted) return;
        productStatus.textContent = "Không tải được sản phẩm. Bạn thử tải lại trang nhé.";
        console.error(error);
    } finally {
        if (productRequest === request) productGrid.setAttribute("aria-busy", "false");
    }
}

function closeSuggestions() {
    clearTimeout(suggestionTimer);
    suggestionRequest?.abort();
    suggestionList.hidden = true;
    suggestionList.replaceChildren();
}

async function loadSuggestions(keyword) {
    const request = new AbortController();
    suggestionRequest = request;

    try {
        const params = new URLSearchParams({ keyword });
        const response = await fetch(`/api/products/suggestions?${params}`, { signal: request.signal });
        if (!response.ok) throw new Error("Không lấy được gợi ý");

        const names = await response.json();
        if (request.signal.aborted) return;

        for (const name of names) {
            const item = document.createElement("li");
            const button = document.createElement("button");
            button.type = "button";
            button.textContent = name;
            button.addEventListener("click", () => {
                searchInput.value = name;
                searchInput.focus();
                searchForm.requestSubmit();
            });
            item.append(button);
            suggestionList.append(item);
        }
        suggestionList.hidden = names.length === 0;
    } catch (error) {
        if (!request.signal.aborted) console.error(error);
    }
}

previousPageButton.addEventListener("click", () => loadProductPage(currentProductPage - 1));
nextPageButton.addEventListener("click", () => loadProductPage(currentProductPage + 1));

searchInput.addEventListener("input", () => {
    closeSuggestions();
    const keyword = searchInput.value.trim();
    if (keyword) suggestionTimer = setTimeout(() => loadSuggestions(keyword), 500);
});

searchForm.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !suggestionList.hidden) {
        event.preventDefault();
        closeSuggestions();
        searchInput.focus();
    }
});

searchForm.addEventListener("focusout", (event) => {
    if (!searchForm.contains(event.relatedTarget)) closeSuggestions();
});

searchForm.addEventListener("submit", (event) => {
    event.preventDefault();
    closeSuggestions();
    searchKeyword = searchInput.value.trim();
    loadProductPage(0);
});

searchInput.value = searchKeyword;
loadProductPage(0);

const categoryGrid = document.getElementById("category-grid");
const categoryPagination = document.getElementById("category-pagination");
const categoryPrevious = document.getElementById("category-previous");
const categoryNext = document.getElementById("category-next");
let currentCategoryPage = 0;
let hasNextCategoryPage = false;

function createCategoryCard(category) {
    const item = document.createElement("li");
    const image = document.createElement("img");
    image.className = "store-category-image";
    image.alt = "";
    image.loading = "lazy";
    image.width = 56;
    image.height = 56;
    const imageUrl = category.hasImage ? `/api/categories/${encodeURIComponent(category.categoryId)}/image` : null;
    setImageSource(image, imageUrl);
    item.append(image, category.categoryName);
    return item;
}

async function loadCategoryPage(page) {
    categoryPrevious.disabled = true;
    categoryNext.disabled = true;
    try {
        const response = await fetch(`/api/categories?page=${page}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        categoryGrid.replaceChildren(...data.content.map(createCategoryCard));
        currentCategoryPage = page;
        hasNextCategoryPage = data.hasNext;
        categoryPagination.hidden = page === 0 && !hasNextCategoryPage;
    } catch (error) {
        // Giữ danh mục đang hiển thị khi API tạm thời không phản hồi.
        console.error("Không tải được danh mục", error);
    } finally {
        categoryPrevious.disabled = currentCategoryPage === 0;
        categoryNext.disabled = !hasNextCategoryPage;
    }
}

categoryPrevious.addEventListener("click", () => loadCategoryPage(currentCategoryPage - 1));
categoryNext.addEventListener("click", () => loadCategoryPage(currentCategoryPage + 1));
loadCategoryPage(0);
