// setInterval(() => {
//     $('.clock_bar').html(moment().format('D.MM.YYYY HH:mm:ss'))
// }, 1000);

setInterval( ()  => {
    // Seconds
    let seconds = new Date().getSeconds();
    document.getElementById("seconds").html((seconds < 10 ? '0' : '') + seconds);

    // Minutes
    let minutes = new Date().getMinutes();
    document.getElementById("minutes").html((minutes < 10 ? '0' : '') + minutes);

    // Hours
    let hours = new Date().getHours();
    document.getElementById("hours").html((hours < 10 ? '0' : '') + hours);
}, 1000);
