const sltYear = document.querySelector("#slt-year");
const sltCountry = document.querySelector("#slt-country");
const sltCity = document.querySelector("#slt-city");

sltYear.addEventListener("change", checkAndGetViews);

sltCountry.addEventListener("change", () => {
    sltCity.options[0].selected = true;
    checkAndGetViews();
    sltCity.disabled = sltCountry.value === "0";
    getCityList(sltCountry.value);

});

sltCity.addEventListener("change", checkAndGetViews);

/* Year, Country <select> 값 체크 후 API 호출 */
function checkAndGetViews() {
    const countryValue = sltCountry.value;
    const yearValue = sltYear.value;
    const cityValue = sltCity.value;

    if (countryValue !== "0" && yearValue !== "0") {
        // Year, Country 둘 다 선택된 경우에만 호출
        getViewsData(yearValue, countryValue, cityValue);
    }
}

/* Views 조회 API 호출 */
function getViewsData (yearVal, countryVal, cityVal) {

    console.log(sltYear.value);
    console.log(sltCountry.value);
    console.log(sltCity.value);

    const inData = {
        year: yearVal,
        country: countryVal,
        city: cityVal
    }

    const param = new URLSearchParams(inData).toString();
    const url = "/api/city/chart?" + param;
    fetch(url, {
        method: "GET"

    }).then(response => {
        if(!response.ok) throw new Error("getViewsData response was not OK");
        return response.json();

    }).then(jsonData => {
        console.log(jsonData);
        console.log(jsonData.cities);

        myLineChart.data.labels = jsonData.labels;
        myLineChart.data.datasets[0].data = jsonData.data;
        myLineChart.update();

    }).catch(error => {
        console.error('getViewsData Error: ' + error);
    });

}

function getCityList(param) {

    const url = "/api/city/list?" + param;
    fetch(url, {
        method: "GET"

    }).then(response => {
        if(!response.ok) throw new Error("getCityList response was not OK");
        return response.json();

    }).then(jsonData => {
        sltCity.innerHTML = '';
        const defaultOption = document.createElement('option');
        defaultOption.value = "0";
        defaultOption.innerText = "전체";
        sltCity.append(defaultOption);

        for(let i=0; i<jsonData.cities.length; i++) {
            const option = document.createElement('option');
            option.innerText = jsonData.cities[i];
            sltCity.append(option);
        }

    }).catch(error => {
        console.error('getCityList Error: ' + error);
    })

}

