setInterval(() => {
    $('#clock_bar').html(moment().format('HH:mm:ss DD.MM.YYYY'))
}, 1000);