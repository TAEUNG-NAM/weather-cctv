const originSelect = document.querySelector("#origin");
const destinationSelect = document.querySelector("#destination");
const airlineSelect = document.querySelector("#airline");
const searchScheduleBtn = document.querySelector("#searchScheduleBtn");
const loading = document.querySelector(".loader");
const flightDateOption = document.querySelectorAll("input[name=flightDateOption]");
const flightDate = document.querySelector("#flightDate");

document.addEventListener('DOMContentLoaded', function() {
    getAvailableDestination(originSelect.value);
});

originSelect.addEventListener('change',  () => {
    getAvailableDestination(originSelect.value);
});

searchScheduleBtn.addEventListener('click', () => {
    getFlightSchedules(originSelect.value);
});

flightDate.addEventListener('click', () => {
    console.log("달력 클릭");
});

flightDateOption.forEach( dateOption => {
    dateOption.addEventListener('change', e => {
        const dateRadio = e.currentTarget;
        let flightDate = document.querySelector("#flightDate");

        if(dateRadio.id === "dateAll") {
            flightDate.disabled = true;

        } else if(dateRadio.id === "dateSelect") {
            flightDate.disabled = false;
        }
    });
});

/* 도착지 & 항공사 목록 조회 */
function getAvailableDestination(origin) {
    loading.classList.remove("d-none");
    fetch("/api/search/destinations", {
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify({
            "pSelType" : document.querySelector("input[name=flightDateOption]:checked").value,
            "pFindDate" : document.querySelector("#flightDate").value,
            "pAirport" : origin,
            "pIotype" : "OUT",
            "pAirline" : "",
            "pAgent" : "",
            "pFightNum" : ""
        }),
    })
        .then(response => response.json())
        .then(result => {
            const availableDestinationsList = result?.availableDestinationsList || [];
            const availableAirlineList = result?.availableAirlineList || [];

            while (destinationSelect.firstChild) {
                destinationSelect.removeChild(destinationSelect.firstChild);
            }

            const defaultOption = document.createElement('option');
            defaultOption.value = "";
            defaultOption.innerText = "전체";
            destinationSelect.append(defaultOption);

            availableDestinationsList.forEach(row => {
                if (row?.CITY_KOR && row?.CITY_CODE) {
                    const option = document.createElement('option');
                    option.innerText = row.CITY_KOR;
                    option.value = row.CITY_CODE;
                    destinationSelect.append(option);
                }
            });

            if(origin === 'ICN') {
                destinationSelect.removeChild(destinationSelect.firstChild);
            }

            // 기존 데이터 삭제
            while (airlineSelect.firstChild) {
                airlineSelect.removeChild(airlineSelect.firstChild);
            }

            const defaultOption2 = document.createElement('option');
            defaultOption2.value = "";
            defaultOption2.innerText = "전체";
            airlineSelect.append(defaultOption2);

            availableAirlineList.forEach(row => {
                if(row?.AIRLINE_KOREAN && row?.AIRLINE_CODE2) {
                    const option = document.createElement('option');
                    option.innerText = row.AIRLINE_KOREAN;
                    option.value = row.AIRLINE_CODE2;
                    airlineSelect.append(option);
                }
            });

        }).catch(error => {
            console.error('도착지 데이터를 불러오던 중 에러 발생: ' + error);
        }).finally(() => {
            loading.classList.add("d-none");
    });
}

/* 항공스케줄 조회 */
function getFlightSchedules(origin) {
    loading.classList.remove("d-none");

    const selType = document.querySelector("input[name=flightDateOption]:checked").value;
    let findDate = "";
    if(selType === 'sel') {
        findDate = document.querySelector("#flightDate").value;
    }

    fetch("/api/search/flight-schedules", {
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify({
            "pSelType" : selType,
            "pFindDate" : findDate,
            "pAirport" : origin,
            "pIotype" : "OUT",
            "pAirline" : destinationSelect.value,
            "pAgent" : airlineSelect.value,
            "pFightNum" : ""
        }),
    })
        .then(response => response.json())
        .then(result => {
            renderFlightRows(result);
        }).catch(error => {
            console.error('도착지 데이터를 불러오던 중 에러 발생: ' + error);
        }).finally(() => {
            loading.classList.add("d-none");
    });
}

/* 조회된 운항스케줄 데이터 렌더링 */
function renderFlightRows(schedules) {
    const tbody = document.querySelector(".schedule-table tbody");
    // 기존 데이터 삭제
    while (tbody.firstChild) {
        tbody.removeChild(tbody.firstChild);
    }

    schedules.forEach(flight => {
        const tr = document.createElement("tr");

        // 항공편 정보 (항공사 로고 + 이름 + 노선)
        const flightTd = document.createElement("td");
        const logoImg = document.createElement("img");
        if(flight.AIRPORT === "인천") {
            logoImg.src = "https://www.airport.kr" + flight.INTERNATIONAL_IDX;
            logoImg.width = 50;
        } else {
            logoImg.src = "/assets/img/gallery/airlineIcons/" + flight.AIRLINE_ENGLISH + ".ico";
            logoImg.width = 20;
        }
        logoImg.alt = flight.AIRLINE_KOREAN;
        logoImg.classList.add("me-1");

        flightTd.appendChild(logoImg);
        flightTd.classList.add("text-wrap")
        flightTd.classList.add("text-start")
        flightTd.classList.add("pe-0")
        flightTd.innerHTML += `<a href="${flight.AIRLINE_HOMEPAGE_URL}" target="_blank">${flight.AIRLINE_KOREAN}(${flight.INTERNATIONAL_NUM})</a><br>`;

        const routeSpan = document.createElement("span");
        routeSpan.className = "text-secondary small";
        routeSpan.innerText = `${flight.AIRPORT} - ${flight.CITY}`;
        flightTd.appendChild(routeSpan);
        tr.appendChild(flightTd);

        // 출발시간
        const depTd = document.createElement("td");
        const departureTime = flight.INTERNATIONAL_TIME;
        flight.AIRPORT === "인천" ? depTd.innerText = departureTime : depTd.innerText = departureTime.slice(0, 2) + ":" + departureTime.slice(2);
        tr.appendChild(depTd);

        // 월~일 요일 운항 여부
        const days = ["INTERNATIONAL_MON", "INTERNATIONAL_TUE", "INTERNATIONAL_WED", "INTERNATIONAL_THU", "INTERNATIONAL_FRI", "INTERNATIONAL_SAT", "INTERNATIONAL_SUN"];
        days.forEach(day => {
            const dayTd = document.createElement("td");
            dayTd.className = "flight-icon";
            dayTd.innerText = flight[day] === "Y" ? "✈️" : "";
            tr.appendChild(dayTd);
        });

        // 운항기간
        const periodTd = document.createElement("td");
        if(flight.AIRPORT === "인천") {
            periodTd.innerText = flight.INTERNATIONAL_STDT;
        } else {
            periodTd.innerText = flight.INTERNATIONAL_STDT + " ~ " + flight.INTERNATIONAL_EDDT;
        }
        tr.appendChild(periodTd);

        // 최종적으로 tbody에 tr 추가
        tbody.appendChild(tr);
    });
}
