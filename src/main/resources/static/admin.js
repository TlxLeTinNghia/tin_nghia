const workspace = document.getElementById("admin-workspace");
const accessMessage = document.getElementById("admin-access-message");
const loginLink = document.getElementById("admin-login");
const searchForm = document.getElementById("admin-search");
const keywordInput = document.getElementById("admin-keyword");
const catalogMessage = document.getElementById("admin-catalog-message");
const productSelect = document.getElementById("admin-product");
const productLink = document.getElementById("admin-product-link");
const pager = document.getElementById("admin-pagination");
const previous = document.getElementById("admin-previous");
const next = document.getElementById("admin-next");
const pageLabel = document.getElementById("admin-page");
const panels = document.getElementById("admin-image-panels");
const mainForm = document.getElementById("admin-main-form");
const variantForm = document.getElementById("admin-variant-form");
const variantSelect = document.getElementById("admin-variant");
const noVariants = document.getElementById("admin-no-variants");
const mainPreview = document.getElementById("admin-main-preview");
const variantPreview = document.getElementById("admin-variant-preview");
const mainMessage = document.getElementById("admin-main-message");
const variantMessage = document.getElementById("admin-variant-message");

const createForm = document.getElementById("admin-create");
const createFields = document.getElementById("admin-create-fields");
const createMessage = document.getElementById("admin-create-message");
const createdProductLink = document.getElementById("admin-created-product");
const categorySelect = document.getElementById("create-category");
const manufacturerSelect = document.getElementById("create-manufacturer");
let createOptionsReady = false;
let creatingProduct = false;

const addVariantSection = document.getElementById("admin-add-variant");
const addVariantForm = document.getElementById("admin-add-variant-form");
const addVariantMessage = document.getElementById("admin-add-variant-message");
const variantProductName = document.getElementById("admin-variant-product-name");
let creatingVariant = false;

const editVariantSection = document.getElementById("admin-edit-variant");
const editVariantSelect = document.getElementById("admin-edit-variant-select");
const editVariantForm = document.getElementById("admin-edit-variant-form");
const editVariantMessage = document.getElementById("admin-edit-variant-message");
const editVariantReload = document.getElementById("admin-edit-variant-reload");
let updatingVariant = false;
let expectedStock;
let editRequest;

const defaultImage = "/Images/default-stationery.jpg";
let product;
let page = 0;
let totalPages = 0;
let keyword = "";
let uploading = false;
let catalogRequest;
let selectionRequest;

for (const image of [mainPreview, variantPreview]) {
    image.addEventListener("error", () => {
        if (image.getAttribute("src") !== defaultImage) image.src = defaultImage;
    });
}

function setMessage(element, message, state = "") {
    element.textContent = message;
    element.dataset.state = state;
}

function savingChanges() {
    return uploading || creatingProduct || creatingVariant || updatingVariant;
}

function updateControls() {
    const lookingUp = Boolean(catalogRequest);
    const saving = savingChanges();
    createFields.disabled = saving || !createOptionsReady;
    editVariantSelect.disabled = saving;
    editVariantReload.disabled = saving;
    editVariantForm.querySelector("fieldset").disabled = saving || expectedStock == null;
    addVariantForm.querySelector("fieldset").disabled = saving;
    searchForm.querySelector("button").disabled = saving;
    productSelect.disabled = saving || lookingUp || productSelect.options.length < 2;
    previous.disabled = saving || lookingUp || page === 0;
    next.disabled = saving || lookingUp || page + 1 >= totalPages;
    mainForm.querySelector("fieldset").disabled = saving;
    variantForm.querySelector("fieldset").disabled = saving;
}

function clearSelection() {
    selectionRequest?.abort();
    selectionRequest = undefined;
    product = undefined;
    editRequest?.abort();
    expectedStock = undefined;
    editVariantSection.hidden = editVariantForm.hidden = editVariantReload.hidden = true;
    setMessage(editVariantMessage, "");
    panels.hidden = productLink.hidden = addVariantSection.hidden = true;
    addVariantForm.reset();
    setMessage(addVariantMessage, "");
    mainForm.reset();
    variantForm.reset();
    variantPreview.hidden = true;
    setMessage(mainMessage, "");
    setMessage(variantMessage, "");
}

async function loadProducts(requestedPage) {
    catalogRequest?.abort();
    const request = new AbortController();
    catalogRequest = request;
    clearSelection();
    productSelect.replaceChildren(new Option("Chọn sản phẩm", ""));
    pager.hidden = true;
    updateControls();
    setMessage(catalogMessage, "Đang tìm sản phẩm…");

    try {
        const params = new URLSearchParams({ keyword, page: requestedPage, size: 20 });
        const response = await fetch(`/api/products?${params}`, { signal: request.signal });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        if (request.signal.aborted) return;

        page = data.page;
        totalPages = data.totalPages;
        productSelect.append(...data.content.map(item => new Option(item.productName, item.productId)));
        pager.hidden = totalPages < 2;
        pageLabel.textContent = `Trang ${page + 1} / ${totalPages}`;
        setMessage(catalogMessage, data.content.length ? "" : "Không tìm thấy sản phẩm.");
    } catch (error) {
        if (!request.signal.aborted) setMessage(catalogMessage, "Chưa tải được sản phẩm. Bạn thử tìm lại nhé.", "error");
    } finally {
        if (catalogRequest === request) {
            catalogRequest = undefined;
            updateControls();
        }
    }
}

async function selectProduct(productId) {
    clearSelection();
    setMessage(catalogMessage, "");
    if (!productId) return;

    const request = new AbortController();
    selectionRequest = request;
    setMessage(catalogMessage, "Đang tải biến thể…");

    try {
        const response = await fetch(`/api/products/${encodeURIComponent(productId)}`, { signal: request.signal });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        if (request.signal.aborted) return;

        product = data;
        variantProductName.textContent = data.productName;
        addVariantSection.hidden = false;
        mainPreview.src = data.hasImage ? `/api/products/${data.productId}/image` : defaultImage;
        productLink.href = `/product.html?id=${data.productId}`;
        productLink.hidden = false;
        variantSelect.replaceChildren(new Option("Chọn biến thể", ""));
        variantSelect.append(...data.variants.map(variant => new Option(variant.variantName, variant.variantId)));
        editVariantSelect.replaceChildren(new Option("Chọn biến thể", ""));
        editVariantSelect.append(...data.variants.map(variant => new Option(variant.variantName, variant.variantId)));
        editVariantSection.hidden = data.variants.length === 0;
        noVariants.hidden = data.variants.length > 0;
        variantForm.hidden = !noVariants.hidden;
        panels.hidden = false;
        setMessage(catalogMessage, "");
    } catch (error) {
        if (!request.signal.aborted) setMessage(catalogMessage, "Chưa tải được thông tin sản phẩm.", "error");
    }
}

function denyAccess(message, needsLogin) {
    workspace.hidden = true;
    accessMessage.hidden = false;
    loginLink.hidden = !needsLogin;
    setMessage(accessMessage, message, "error");
}

async function uploadImage(form, message, url, method, onSuccess) {
    if (savingChanges()) return;
    const payload = new FormData(form);
    if (payload.get("file").size > 1024 * 1024) {
        setMessage(message, "Ảnh phải từ 1 MB trở xuống.", "error");
        return;
    }

    uploading = true;
    updateControls();
    setMessage(message, "Đang tải ảnh lên…");
    try {
        const response = await fetch(url, { method, body: payload });
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        const data = await response.json().catch(() => null);
        if (!response.ok) {
            setMessage(message, data?.message ?? "Chưa lưu được ảnh. Kiểm tra tệp rồi thử lại nhé.", "error");
            return;
        }
        onSuccess(data);
        form.querySelector('input[type="file"]').value = "";
        setMessage(message, "Đã lưu ảnh.", "success");
    } catch {
        setMessage(message, "Chưa nhận được phản hồi từ server. Hãy kiểm tra ảnh trước khi thử lại.", "error");
    } finally {
        uploading = false;
        updateControls();
    }
}

searchForm.addEventListener("submit", event => {
    event.preventDefault();
    if (savingChanges()) return;
    keyword = keywordInput.value.trim();
    loadProducts(0);
});
previous.addEventListener("click", () => loadProducts(page - 1));
next.addEventListener("click", () => loadProducts(page + 1));
productSelect.addEventListener("change", () => selectProduct(productSelect.value));
variantSelect.addEventListener("change", () => {
    variantPreview.hidden = true;
    setMessage(variantMessage, "");
});

mainForm.addEventListener("submit", event => {
    event.preventDefault();
    if (!product) return;
    const productId = product.productId;
    uploadImage(mainForm, mainMessage, `/api/admin/products/${productId}/image`, "PUT", () => {
        mainPreview.src = `/api/products/${productId}/image?v=${Date.now()}`;
    });
});
variantForm.addEventListener("submit", event => {
    event.preventDefault();
    const variantId = variantSelect.value;
    if (!variantId) return;
    uploadImage(variantForm, variantMessage, `/api/admin/variants/${encodeURIComponent(variantId)}/images`, "POST", result => {
        variantPreview.src = `/api/variants/${result.imageId}/image`;
        variantPreview.hidden = false;
    });
});

function readVariantFields(fields) {
    return {
        variantName: fields.get("variantName").trim(),
        unitPrice: fields.get("unitPrice"),
        unitsInStock: Number(fields.get("unitsInStock")),
        discountPercent: fields.get("discountPercent") || "0"
    };
}

addVariantForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!product || savingChanges()) return;

    const payload = readVariantFields(new FormData(addVariantForm));
    const productId = encodeURIComponent(product.productId);
    creatingVariant = true;
    updateControls();
    setMessage(addVariantMessage, "Đang lưu biến thể…");

    try {
        const response = await fetch(`/api/admin/products/${productId}/variants`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        const result = await response.json().catch(() => null);
        if (!response.ok) {
            setMessage(addVariantMessage, result?.message ?? "Chưa lưu được biến thể. Bạn kiểm tra lại thông tin nhé.", "error");
            return;
        }
        if (!Number.isInteger(result?.variantId) || result.variantId <= 0) {
            throw new Error("Thiếu mã biến thể trong phản hồi");
        }

        variantForm.reset();
        variantSelect.add(new Option(payload.variantName, result.variantId, false, true));
        editVariantSelect.add(new Option(payload.variantName, result.variantId));
        editVariantSection.hidden = false;
        variantSelect.dispatchEvent(new Event("change"));
        noVariants.hidden = true;
        variantForm.hidden = false;
        addVariantForm.reset();
        setMessage(addVariantMessage, "Đã lưu biến thể và chọn sẵn ở phần tải ảnh.", "success");
    } catch {
        setMessage(addVariantMessage, "Chưa nhận được phản hồi. Hãy kiểm tra danh sách biến thể trước khi thử lưu lại.", "error");
    } finally {
        creatingVariant = false;
        updateControls();
    }
});

async function loadVariantForEdit() {
    editRequest?.abort();
    expectedStock = undefined;
    updateControls();
    editVariantForm.hidden = true;
    editVariantReload.hidden = true;
    setMessage(editVariantMessage, "");
    const variantId = editVariantSelect.value;
    if (!product || !variantId) return;
    const productId = product.productId;
    const request = new AbortController();
    editRequest = request;
    setMessage(editVariantMessage, "Đang tải thông tin biến thể…");
    try {
        const response = await fetch(`/api/admin/products/${productId}/variants/${encodeURIComponent(variantId)}`, {
            cache: "no-store",
            signal: request.signal
        });
        if (request.signal.aborted) return;
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const current = await response.json();
        if (request.signal.aborted) return;
        editVariantForm.elements.variantName.value = current.variantName;
        editVariantForm.elements.unitPrice.value = current.unitPrice;
        editVariantForm.elements.unitsInStock.value = current.unitsInStock;
        editVariantForm.elements.discountPercent.value = current.discountPercent;
        expectedStock = current.unitsInStock;
        updateControls();
        editVariantForm.hidden = false;
        setMessage(editVariantMessage, "");
    } catch {
        if (request.signal.aborted) return;
        setMessage(editVariantMessage, "Chưa tải được thông tin biến thể.", "error");
        editVariantReload.hidden = false;
    }
}

editVariantSelect.addEventListener("change", loadVariantForEdit);
editVariantReload.addEventListener("click", loadVariantForEdit);
editVariantForm.addEventListener("submit", async event => {
    event.preventDefault();
    const variantId = editVariantSelect.value;
    if (!product || !variantId || expectedStock == null || savingChanges()) return;
    const variant = readVariantFields(new FormData(editVariantForm));
    updatingVariant = true;
    updateControls();
    setMessage(editVariantMessage, "Đang lưu thay đổi…");

    try {
        const response = await fetch(`/api/admin/products/${product.productId}/variants/${encodeURIComponent(variantId)}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variant, expectedUnitsInStock: expectedStock })
        });
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        if (!response.ok) {
            const error = await response.json().catch(() => null);
            setMessage(editVariantMessage, error?.message ?? "Chưa lưu được biến thể.", "error");
            if (response.status === 409) {
                expectedStock = undefined;
    updateControls();
                editVariantReload.hidden = false;
            }
            return;
        }

        expectedStock = variant.unitsInStock;
        editVariantSelect.selectedOptions[0].text = variant.variantName;
        const uploadOption = [...variantSelect.options].find(option => option.value === variantId);
        if (uploadOption) uploadOption.text = variant.variantName;
        setMessage(editVariantMessage, "Đã cập nhật biến thể.", "success");
    } catch {
        expectedStock = undefined;
    updateControls();
        editVariantReload.hidden = false;
        setMessage(editVariantMessage, "Chưa nhận được phản hồi. Hãy tải lại thông tin trước khi thử.", "error");
    } finally {
        updatingVariant = false;
        updateControls();
    }
});

async function loadCreateOptions() {
    setMessage(createMessage, "Đang tải danh mục và hãng sản xuất…");
    try {
        const response = await fetch("/api/admin/products/options", { cache: "no-store" });
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const { categories, manufacturers } = await response.json();
        categorySelect.append(...categories.map(({id, name}) => new Option(name, id)));
        manufacturerSelect.append(...manufacturers.map(({id, name}) => new Option(name, id)));
        createOptionsReady = categories.length > 0 && manufacturers.length > 0;
        setMessage(createMessage, createOptionsReady ? "" : "Cần có ít nhất một danh mục và một hãng sản xuất.", "error");
        updateControls();
    } catch {
        setMessage(createMessage, "Chưa tải được danh mục và hãng sản xuất. Bạn thử tải lại trang nhé.", "error");
    }
}

createForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!createOptionsReady || savingChanges()) return;
    const fields = new FormData(createForm);
    const productName = fields.get("productName").trim();
    const payload = {
        productName,
        description: fields.get("description").trim(),
        categoryId: Number(fields.get("categoryId")),
        manufacturerId: Number(fields.get("manufacturerId")),
        variant: readVariantFields(fields)
    };

    creatingProduct = true;
    createdProductLink.hidden = true;
    updateControls();
    setMessage(createMessage, "Đang tạo sản phẩm…");
    try {
        const response = await fetch("/api/admin/products", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (response.status === 401 || response.status === 403) {
            denyAccess(response.status === 401 ? "Phiên đăng nhập đã hết." : "Bạn không có quyền quản lý sản phẩm.", response.status === 401);
            return;
        }
        const result = await response.json().catch(() => null);
        if (!response.ok) {
            setMessage(createMessage, result?.message ?? "Chưa tạo được sản phẩm. Bạn kiểm tra lại thông tin nhé.", "error");
            return;
        }

        const id = String(result.productId);
        createForm.reset();
        createdProductLink.href = `/product.html?id=${encodeURIComponent(id)}`;
        createdProductLink.hidden = false;
        setMessage(createMessage, "Đã tạo sản phẩm và biến thể đầu tiên.", "success");
        keyword = keywordInput.value = productName;
        await loadProducts(0);
        if (![...productSelect.options].some(option => option.value === id)) {
            productSelect.add(new Option(productName, id));
        }
        productSelect.value = id;
        await selectProduct(id);
    } catch {
        setMessage(createMessage, "Chưa nhận được phản hồi. Hãy tìm sản phẩm trước khi thử lưu lại.", "error");
    } finally {
        creatingProduct = false;
        updateControls();
    }
});

async function loadAdmin() {
    try {
        const response = await fetch("/api/auth/me", { cache: "no-store" });
        if (response.status === 401) {
            denyAccess("Bạn cần đăng nhập bằng tài khoản quản trị.", true);
            return;
        }
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const user = await response.json();
        if (user.role !== "ADMIN") {
            denyAccess("Bạn không có quyền quản lý sản phẩm.", false);
            return;
        }
        accessMessage.hidden = true;
        workspace.hidden = false;
        loadProducts(0);
        loadCreateOptions();
    } catch {
        denyAccess("Chưa xác nhận được quyền truy cập. Bạn thử tải lại trang nhé.", false);
    }
}

loadAdmin();
