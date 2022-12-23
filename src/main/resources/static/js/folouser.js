function setCheckboxes() {
    document.getElementsByName('anchor').forEach(function(element, idx) {
        if (element.value === 'true') {
            element.checked = element.value
        }
    })
}

function onCheckboxClick(element) {
    element.value = element.checked;
}
