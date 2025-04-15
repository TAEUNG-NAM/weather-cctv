// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

// Pie Chart Example
const pieCtx = document.getElementById("myPieChart");
const myPieChart = new Chart(pieCtx, {
  type: 'doughnut',
  data: {
    labels: [], // 데이터 라벨
    datasets: [{
      data: [], // 데이터 값
      backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#ffa0c0'],
      hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf', '#f4b619', '#f56b9a'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: true,
    tooltips: {
      callbacks: {
        label: function(tooltipItem, data) {
          const dataset = data.datasets[0];
          const total = dataset.data.reduce((acc, value) => acc + value, 0); // 전체 데이터 합 계산
          const currentValue = dataset.data[tooltipItem.index];
          const percentage = ((currentValue / total) * 100).toFixed(2); // 비율 계산
          return `${data.labels[tooltipItem.index]}: ${currentValue} (${percentage}%)`; // 라벨에 값과 비율 표시
        },
        footer: function(tooltipItems, data) {
          const dataset = data.datasets[0];
          const total = dataset.data.reduce((acc, value) => acc + value, 0); // 전체 데이터 합 계산
          return `Total: ${total}`; // 툴팁 하단에 합계 표시
        }
      },
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      footerFontColor: "#858796", // 툴팁 하단 텍스트 색상
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 25,
      yPadding: 25,
      displayColors: false,
      caretPadding: 15,
    },
    legend: {
      display: false, // 범례 숨김
    },
    cutoutPercentage: 80, // 도넛 중앙 비율
  }
});


