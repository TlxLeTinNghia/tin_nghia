const bannerImages = document.querySelectorAll(".hero-banner img");
// id dùng # còn class thì dùng .
const inputKeyword = document.getElementById("search-input");
const nextPageButton = document.querySelector("#next-page");
const previousPageButton = document.querySelector("#previous-page");
const pageInfo = document.querySelector("#page-info");
const searchForm = document.querySelector("#search-form");
const suggestionList = document.getElementById("suggestions");
let currentIndexBannerImage = 0;
let currentProductPage = 0;
let productPageSize = 2;
let searchKeyword ="";
let suggestionTimer;
if(bannerImages.length > 1){
    setInterval(()=>{
        bannerImages[currentIndexBannerImage].hidden = true;
        currentIndexBannerImage = (currentIndexBannerImage +1)% bannerImages.length;
        bannerImages[currentIndexBannerImage].hidden = false;
    },3000)
}

async function loadProductPage(page){
    try{
        const params = new URLSearchParams({
            page :page,
            size : productPageSize,
            keyword : searchKeyword
        });
        const response = await fetch(`/api/products?${params.toString()}`)
        if(!response.ok){
            throw new Error(`Không tải được sản phẩm(HTTP ${response.status}`);
        }
        const data = await response.json();
        const products = data.content;
        const productPage = document.querySelector(".product-grid");
        productPage.replaceChildren();
        for(const product of products){
            const productName = document.createElement("h3");
            productName.textContent = product.productName;
            const description = document.createElement("p");
            description.textContent = product.description;
            const productRating = document.createElement("p");
            productRating.textContent = product.reviewCount === 0 ? 'Chưa có đánh giá!' :
                `${product.averageRating}/5 - ${product.reviewCount} lượt đánh giá`;
            const card = document.createElement("article");
            const img = document.createElement("img");
            img.src = product.hasImage ? `/api/products/${product.productId}/image` : "/Images/pic2.jpg";
            card.append(img, productName ,description , productRating );
            productPage.append(card);
            card.addEventListener("click",()=>{
                window.location.href = `/product.html?id=${product.productId}`;
            })
        }
        currentProductPage = data.page;
        if(currentProductPage === 0)
            previousPageButton.disabled = true;
        else previousPageButton.disabled = false;
        if(currentProductPage < (data.totalPages -1))
            nextPageButton.disabled = false;
        else nextPageButton.disabled = true;
        pageInfo.textContent = (data.totalPages===0)?"Không có sản phẩm!":`Trang ${currentProductPage + 1} / ${data.totalPages}.`;
        console.log(data);

    }catch (error){
        console.error(error);
    }
}

async function loadSuggestion(keyword){
    try{
        const params = new URLSearchParams({
            keyword : keyword
        });
        const response = await fetch(`/api/products/suggestions?${params.toString()}`);
        if(!response.ok){
            throw new Error("Không lấy được gợi ý");
        }
        const names = await response.json();
        suggestionList.replaceChildren();
        for(const name of names){
            const item = document.createElement("li");
            const button = document.createElement("button");
            button.type = "button";
            button.textContent = name;
            item.append(button);
            button.addEventListener("click",()=>{
                inputKeyword.value = button.textContent;
                suggestionList.replaceChildren();
                suggestionList.hidden = true;
                searchForm.requestSubmit();
            });
            suggestionList.append(item);
        }
        suggestionList.hidden = (names.length === 0);
    }catch (error){
        console.error(error);
    }
}

previousPageButton.addEventListener("click",()=>{
    loadProductPage(currentProductPage-1);
});

nextPageButton.addEventListener("click" , ()=>{
    loadProductPage(currentProductPage+1);
});

searchForm.addEventListener("submit",(event)=>{
    event.preventDefault(); // chặn cái hành động mặc định của button
    searchKeyword = inputKeyword.value.trim();
    loadProductPage(0);
});

inputKeyword.addEventListener("input",()=>{
    clearTimeout(suggestionTimer);
    suggestionTimer = setTimeout(()=>{
        console.log(inputKeyword.value.trim());
        loadSuggestion(inputKeyword.value.trim());
    },500)
});

const urlParams = new URLSearchParams(window.location.search);
searchKeyword = (urlParams.get("keyword")??"").trim();
inputKeyword.value = searchKeyword;

loadProductPage(0);