initPoints(149);
drawFrame();
setTimeout(function () {
    $(".tooltip").hide();
}, 10_000)

var a = "登录";
var b = "注册";
var tip = a;
$('.toggle').click(function () {
    $("#fgt").toggle();
    $(".tooltip").hide();
    $(this).children('i').text(tip);
    tip = b;
    b = a;
    a = tip;
    $('.form').animate({
        height: "toggle",
        'padding-top': 'toggle',
        'padding-bottom': 'toggle',
        opacity: "toggle"
    }, "slow");
});

function login() {
    $.ajax({
        type: 'POST',
        url: 'login',
        // dataType: 'json',
        // contentType: 'application/json',
        data: {
            username: $("#username").val(),
            password: encrypt($("#password").val())
        },
        async: false,
        success: function (data) {
            if (data.code === 200) {
                window.location.href = "/";
            } else if (data.code === 500) {
                window.location.href = "error/500.html"
            } else {
                alert(data.message);
            }
        }
    });
}

function register() {
    $.ajax({
        type: 'POST',
        url: 'user/register',
        // dataType: 'json',
        // contentType: 'application/json',
        data: {
            username: $("#rusername").val(),
            password: encrypt($("#rpassword").val()),
            nickname: $("#rnickname").val(),
            mailbox: $("#remail").val(),
            mobile: $("#rmobile").val()
        },
        async: false,
        success: function (data) {
            if (data.code === 200) {
                window.location.href = "/";
            } else if (data.code === 500) {
                window.location.href = "error/500.html"
            } else {
                alert(data.message);
            }
        }
    });
}

function forget() {
    if (!confirm("删库跑路了, 密码没有了")) {
        alert("emmmm…………要不就算了吧");
    }
}

// 绑定发送按钮回车事件
$('#dologin').keydown(function (e) {
    // console.log(e.keyCode);
    if (e.keyCode === 13) {
        $('#dologin').trigger('click');
        e.preventDefault(); //屏蔽enter对系统作用。按后增加\r\n等换行
    }
});
