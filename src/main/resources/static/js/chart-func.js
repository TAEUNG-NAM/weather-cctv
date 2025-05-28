const sltYear = document.querySelector("#slt-year");
const sltCountry = document.querySelector("#slt-country");
const sltCity = document.querySelector("#slt-city");

sltYear.addEventListener("change", checkAndGetViews);

sltCountry.addEventListener("change", () => {
    sltCity.options[0].selected = true;
    checkAndGetViews();
    sltCity.disabled = sltCountry.value === "country";
    getCityList(sltCountry.value);

});

sltCity.addEventListener("change", checkAndGetViews);

/* Year, Country, City <select> 값 체크 후 조회수 및 CCTV 정보 호출 */
function checkAndGetViews() {
    const countryValue = sltCountry.value;
    const yearValue = sltYear.value;
    const cityValue = sltCity.value;

    // 도시를 선택했을 때 CCTV Row 추가 버튼 활성화
    if(cityValue === "total") {
        addRowBtn.disabled = true;
        saveCctvDataBtn.style.display = 'none';
        saveCctvDataBtn.disabled = true;
    } else {
        addRowBtn.disabled = false;
        saveCctvDataBtn.disabled = false;
        saveCctvDataBtn.style.display = 'block';
    }

    if (countryValue !== "country" && yearValue !== "year") {
        // Year, Country 둘 다 선택된 경우에만 호출
        getViewsAreaData(yearValue, countryValue, cityValue);
        getViewsDonutData(yearValue, countryValue);
        getCctvData(cityValue);   // 도시별 CCTV 정보(목록) 가져오기
    }
}

/* AreaChart(Views) 조회 API 호출 */
function getViewsAreaData (yearVal, countryVal, cityVal) {
    const inData = {
        year: yearVal,
        country: countryVal,
        city: cityVal
    }

    const param = new URLSearchParams(inData).toString();
    const url = "/api/admin/area-chart/views?" + param;
    fetch(url, {
        method: "GET"

    }).then(response => {
        if(!response.ok) throw new Error("getViewsData response was not OK");
        return response.json();

    }).then(jsonData => {
        // console.log(jsonData);
        // console.log(jsonData.cities);

        myLineChart.data.labels = jsonData.labels;
        myLineChart.data.datasets[0].data = jsonData.data;
        myLineChart.update();

    }).catch(error => {
        console.error('getViewsData Error: ' + error);
    });

}

/* DonutChart(Views) 조회 API 호출 */
function getViewsDonutData (yearVal, countryVal) {

    // console.log(sltYear.value);
    // console.log(sltCountry.value);

    const inData = {
        year: yearVal,
        country: countryVal
    }

    const param = new URLSearchParams(inData).toString();
    const url = "/api/admin/donut-chart/views?" + param;
    fetch(url, {
        method: "GET"

    }).then(response => {
        if(!response.ok) throw new Error("getViewsData response was not OK");
        return response.json();

    }).then(jsonData => {
        // console.log(jsonData);
        myPieChart.data.labels = jsonData.labels;
        myPieChart.data.datasets[0].data = jsonData.data;
        myPieChart.update();

    }).catch(error => {
        console.error('getViewsData Error: ' + error);
    });

}


// 선택한 국가의 도시 목록<Select>
function getCityList(countryVal) {

    const inData = {
        country: countryVal
    }
    const param = new URLSearchParams(inData).toString();
    const url = "/api/admin/area-chart/city-list?" + param;
    fetch(url, {
        method: "GET"

    }).then(response => {
        if(!response.ok) throw new Error("getCityList response was not OK");
        return response.json();

    }).then(jsonData => {
        sltCity.innerHTML = '';
        const defaultOption = document.createElement('option');
        defaultOption.value = "total";
        defaultOption.innerText = "전체";
        sltCity.append(defaultOption);

        for(let i=0; i<jsonData.length; i++) {
            const option = document.createElement('option');
            option.innerText = jsonData[i].name;
            option.value = jsonData[i].name;
            sltCity.append(option);
        }

    }).catch(error => {
        console.error('getCityList Error: ' + error);
    })

}

