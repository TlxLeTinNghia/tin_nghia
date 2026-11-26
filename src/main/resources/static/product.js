const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("id");
const defaultImage = '/Images/pic1.jpg';

const minusButton = document.getElementById("quantity-minus");
const plusButton = document.getElementById("quantity-plus");
const quantityValue = document.getElementById("quantity-value");
const productPrice = document.querySelector(".product-price");

const buyNow = document.getElementById("buy-now");

let quantity = 1;
let selectVariant = null;
const formatPrice = Intl.NumberFormat("vi-VN",{
    style: "currency",
    currency : "VND"
});
async function  loadProductDetail(productId) {
    if (!productId) {
        return;
    }
    try {
        const response = await fetch(`/api/products/${productId}`);
        if (!response.ok) {
            throw new Error(`Không tải được chi tiết sản phẩm! (HTTP ${response.status})`);
        }
        const data = await response.json();
        const imageIds = data.imageIds;
        const mainImage = document.querySelector(".product-image img");
        const variantImage = document.querySelector(".variant-image");
        variantImage.replaceChildren();
        for(const imageId of imageIds){
            const item = document.createElement("li");
            const img = document.createElement("img");
            img.addEventListener("error",()=>{
                img.src = defaultImage;
            },{once:true});
            img.src = `/api/variants/${imageId}/image`;
            img.addEventListener("click",()=>{
                // hiện ảnh lên khung lớn
                mainImage.src = img.src;
            });
            item.append(img);
            variantImage.append(item);
        }
        const productName = document.querySelector(".product-name");
        productName.textContent = data.productName;
        const productDescription = document.querySelector(".product-description");
        productDescription.textContent = data.description;
        const productRating = document.querySelector(".product-rating p");
        if (data.reviewCount === 0) {
            productRating.textContent = 'Chưa có đánh giá';
        } else {
            productRating.textContent = `${data.averageRating}/5 - ${data.reviewCount} lượt đánh giá`;
        }
        const variants = data.variants;
        const productVariant = document.querySelector(".product-variants");
        productVariant.replaceChildren();
        for (const variant of variants) {
            const item = document.createElement("li");
            const button = document.createElement("button");
            button.type = "button";
            button.classList.add("variant-button");
            button.addEventListener("click",()=>{
                quantity =1;
                selectVariant = variant;
                updatePrice();
            });
            button.disabled = variant.unitsInStock === 0;
            const image = document.createElement("img");
            await loadVariantThumbnail(image , variant.variantId);
            image.alt = "Ảnh biến thể";
            const variantName = document.createElement("span");
            variantName.textContent = variant.variantName;
            const stock = document.createElement("span");
            stock.textContent = ` Còn ${variant.unitsInStock}`;
            button.append(image, variantName, stock);
            item.append(button);
            productVariant.append(item);
        }
        updatePrice();
        console.log(data);
    } catch (error) {
        console.error(error);
    }
}
async function loadVariantThumbnail(image , variantId){
    image.src = defaultImage;
    try{
        const response = await fetch(`/api/variants/${variantId}/images`);
        if(!response.ok) return;
        const images = await response.json();
        const imageId = images[0]?.imageId;
        if(imageId == null) return;
        image.addEventListener("error",()=>{
            image.src = defaultImage;
        },{once: true});
        image.src = `/api/variants/${imageId}/image`;
    }catch (error){
        console.error("Không lấy được biến thể !",error);
    }
}



function updatePrice(){
    quantityValue.textContent = quantity;
    minusButton.disabled = !selectVariant || quantity <= 1;
    plusButton.disabled = !selectVariant || selectVariant.unitsInStock <= quantity;
    if(!selectVariant){
        productPrice.textContent = 'Chọn loại sản phẩm để xem giá!';
        return;
    }
    const unitPrice = selectVariant.unitPrice * (1-selectVariant.discount);
    const total = unitPrice*quantity;
    productPrice.textContent = `Đơn giá ${formatPrice.format(unitPrice)} ~ Tạm tính : ${formatPrice.format(total)}`;
}

minusButton.addEventListener("click", ()=>{
    quantity--;
    updatePrice();
});

plusButton.addEventListener("click", ()=>{
    quantity++;
    updatePrice();
});

buyNow.addEventListener("click",async ()=>{
    if(!selectVariant){
        console.log("Bạn chưa chọn phân loại!");
        return;
    }

    buyNow.disabled = true;

    try{
        const response = await fetch("/api/orders",{
            method : "POST",
            headers : {"Content-Type":"application/json"},
            body : JSON.stringify({
                "variantId" : selectVariant.variantId,
                "quantity" : quantity
            })
        });

        const data = response.json();

        if(!response.ok){
            alert(data.message??"Không tạo được đơn hàng");
        }

    }catch (e){
        console.error(e);
        alert("Không nhận được phản hồi đặt hàng, hãy kiểm tra đơn trước khi thử lại!")
    } finally {
        buyNow.disabled = false;
        // finally là khối lệnh chạy khi thoát khỏi khối try
    }
});

loadProductDetail(productId);