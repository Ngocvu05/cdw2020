$(document).ready(function() {
    $('#productName').autocomplete({
        source : "search",
        param :"ten",
        minLength :1,
    });
});