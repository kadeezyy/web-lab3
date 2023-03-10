window.addEventListener('load', () => document.querySelector('svg').addEventListener('click', event => {
    const position = getCursorPosition(event)
    // const r = $("label[for='" + $('[name="value_R"]:checked').attr('id') + "']").html()
    const r = document.getElementById('main_form:spinner_input').getAttribute('aria-valuenow')
    const x = (position.x - 200) / 140 * r
    const y = (200 - position.y) / 140 * r
    // const position = getCursorPosition(event)
    // const x = (position.x - 150) / 100 * r
    // const y = (150 - position.y) / 100 * r
    addResultFromPlot([
        {name: 'x', value: x.toFixed(3)},
        {name: 'y', value: y.toFixed(3)}
    ])
}))

function renderPlot(results, r) {
    document.getElementById('dots').innerHTML = ''
    results.forEach(result => {
        const x = (result.x * 140 / r) + 200
        const y = (result.y * -140 / r) + 200
        const color = result.successful ? 'green' : 'red'
        createPointer(x, y, color)
    })
}

function getCursorPosition(event) {
    const rect = document.querySelector('svg').getBoundingClientRect()
    return {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };
}

function createPointer(x, y, color) {
    const pointer = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
    pointer.setAttribute('cx', x)
    pointer.setAttribute('cy', y)
    pointer.setAttribute('r', '3')
    pointer.setAttribute('fill', color)
    document.getElementById('dots').appendChild(pointer)
}