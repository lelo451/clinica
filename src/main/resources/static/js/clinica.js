/* <![CDATA[ */
function validaCPF() {
    var value = $('#cpf').val();
    value = value.replace(/\D/g, '');

    if (!/^\d{11}$/.test(value) || /^1{11}|2{11}|3{11}|4{11}|5{11}|6{11}|7{11}|8{11}|9{11}|0{11}$/.test(value)) {
        return false;
    }

    var d1 = 0;
    for (var i = 0; i < 9; i++) {
        d1 += (10 - i) * parseInt(value.charAt(i), 10);
    }
    d1 = 11 - d1 % 11;
    if (d1 === 10 || d1 === 11) {
        d1 = 0;
    }
    if (d1 + '' !== value.charAt(9)) {
        return false;
    }

    var d2 = 0;
    for (i = 0; i < 10; i++) {
        d2 += (11 - i) * parseInt(value.charAt(i), 10);
    }
    d2 = 11 - d2 % 11;
    if (d2 === 10 || d2 === 11) {
        d2 = 0;
    }

    return (d2 + '' === value.charAt(10));
}
function getToday() {
    var d = new Date();
    var month = d.getMonth() + 1;
    var day = d.getDate();
    return (day < 10 ? '0' : '') + day + '/' + (month < 10 ? '0' : '') + month + '/' + d.getFullYear();
}
function sleep(miliseconds) {
    var currentTime = new Date().getTime();
    while (currentTime + miliseconds >= new Date().getTime()) {
    }
}
/* ]]> */