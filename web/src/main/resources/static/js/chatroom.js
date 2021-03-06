var chaturi;
var userId;
var socket;
var sentMessageMap;

/**
 * 同步请求
 */
function initChatURI() {
    $.ajax({
        type: "POST",
        url: "chat/groupchat/path",
        dataType: "json",
        async: true,
        success: function (data) {
            if (data.code === 200) {
                chaturi = data.data;
                initUserInfo();
                startConnectChatServer();
            } else {
                console.log(data);
                alert(data.message);
            }
        }
    });
}

function initUserInfo() {
    $.ajax({
        type: 'POST',
        url: 'user/fetch/info',
        dataType: 'json',
        async: true,
        success: function (data) {
            if (data.code === 200) {
                var userInfo = data.data;
                userId = userInfo.uid;
                $("#username").html(userInfo.username);
                $("#avatarUrl").attr("src", userInfo.avatar);
                var groupListHTML = "";
                var groupList = userInfo.groupList;
                for (var i = 0; i < groupList.length; i++) {
                    groupListHTML +=
                        '<li>' +
                        '<div class="liLeft"><img src="' + groupList[i].groupAvatarUrl + '"></div>' +
                        '<div class="liRight">' +
                        '<span class="hidden-groupId">' + groupList[i].groupId + '</span>' +
                        '<span class="intername">' + groupList[i].groupName + '(' + groupList[i].members.length + ')</span>' +
                        '<span class="infor"></span>' +
                        '</div>' +
                        '</li>';
                }
                $('.conLeft ul').append(groupListHTML);

                var friendListHTML = "";
                var friendList = userInfo.friendList;
                for (var i = 0; i < friendList.length; i++) {
                    friendListHTML +=
                        '<li>' +
                        '<div class="liLeft"><img src="' + friendList[i].avatar + '"></div>' +
                        '<div class="liRight">' +
                        '<span class="hidden-userId">' + friendList[i].uid + '</span>' +
                        '<span class="intername">' + friendList[i].nickname + '</span>' +
                        '<span class="infor"></span>' +
                        '</div>' +
                        '</li>';
                }
                //
                initSentMessageMap(data.data);
                // 设置好友列表
                $('.conLeft ul').append(friendListHTML);
                // 绑定好友框点击事件
                $('.conLeft ul li').on('click', friendLiClickEvent);
                // 绑定输入框以及右侧面板点击事件
                $('.conRight').on('click', messageDivClickEvent);
                //默认第一个展开面板
                $('.conLeft ul li').first().trigger("click");
            } else {
                alert(data.message);
            }
        }
    });
}

function initSentMessageMap(data) {
    sentMessageMap = new SentMessageMap();
    var groupList = data.groupList;
    if (groupList && groupList.length > 0) {
        for (var i = 0; i < groupList.length; i++) {
            sentMessageMap.put(groupList[i].groupId, []);
        }
    }
    var friendList = data.friendList;
    if (friendList && friendList.length > 0) {
        for (var j = 0; j < friendList.length; j++) {
            sentMessageMap.put(friendList[j].uid, []);
        }
    }
    console.log(sentMessageMap);
}

function startConnectChatServer() {
    if (!chaturi) {
        alert("没有有效地址！");
        return;
    }
    if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket;
    }
    if (window.WebSocket) {
        socket = new WebSocket(chaturi);
        socket.onmessage = function (event) {
            var json = JSON.parse(event.data);
            if (json.code === 200) {
                var type = json.type;
                console.log("收到一条新信息，类型为：" + type);
                switch (type) {
                    case 0:
                        ws.registerReceive();
                        break;
                    case 1:
                        ws.singleReceive(json);
                        break;
                    case 2:
                        ws.groupReceive(json);
                        break;
                    case 3:
                        ws.fileMsgSingleReceive(json);
                        break;
                    case 4:
                        ws.fileMsgGroupReceive(json);
                        break;
                    case 5:
                        ws.singleEmojiReceive(json);
                        break;
                    case 6:
                        ws.groupEmojiReceive(json);
                        break;
                    default:
                        if (json.code === 9999) {
                            alert(json.message)
                        } else {
                            alert("错误:" + json);
                        }
                        break;
                }
            } else {
                alert(json.message);
                console.log(json.message);
            }
        };

        // 连接成功半秒后，将用户信息注册到服务器在线用户表
        socket.onopen = setTimeout(function (event) {
            console.log("WebSocket已成功连接！");
            ws.register();
        }, 500);

        socket.onclose = function (event) {
            console.log("WebSocket已关闭...");
        };
    } else {
        alert("您的浏览器不支持WebSocket！");
    }
}

var ws = {
    register: function () {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 0,
                "sourceUid": userId
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },

    singleSend: function (targetUid, content) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 1,
                "sourceUid": userId,
                "targetUid": targetUid,
                "content": content
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },
    singleEmojiSend: function (targetUid, content) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 5,
                "sourceUid": userId,
                "targetUid": targetUid,
                "content": content
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },
    groupEmojiSend: function (targetGid, content) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 6,
                "sourceUid": userId,
                "targetGid": targetGid,
                "content": content
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },
    groupSend: function (targetGid, content) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 2,
                "sourceUid": userId,
                "targetGid": targetGid,
                "content": content
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },

    fileMsgSingleSend: function (targetUid, fileName, fileURL, fileSize) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 3,
                "sourceUid": userId,
                "targetUid": targetUid,
                "fileName": fileName,
                "fileURL": fileURL,
                "fileSize": fileSize
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },

    fileMsgGroupSend: function (targetGid, fileName, fileURL, fileSize) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState === WebSocket.OPEN) {
            var data = {
                "type": 4,
                "sourceUid": userId,
                "targetGid": targetGid,
                "fileName": fileName,
                "fileURL": fileURL,
                "fileSize": fileSize
            };
            socket.send(JSON.stringify(data));
        } else {
            alert("Websocket连接没有开启！");
        }
    },

    registerReceive: function () {
        console.log("userId为 " + userId + " 的用户登记到在线用户表成功！");
    },

    singleReceive: function (data) {
        // 获取、构造参数
        console.log(data);
        var fromUserId = data.sourceUid;
        var content = data.content;
        var fromAvatarUrl;
        var $receiveLi;
        $('.conLeft').find('span.hidden-userId').each(function () {
            if (this.innerHTML == fromUserId) {
                fromAvatarUrl = $(this).parent(".liRight")
                    .siblings(".liLeft").children('img').attr("src");
                $receiveLi = $(this).parent(".liRight").parent("li");
            }
        });
        var answer = '';
        answer += '<li>' +
            '<div class="answers">' + content + '</div>' +
            '<div class="answerHead"><img src="' + fromAvatarUrl + '"/></div>' +
            '</li>';

        // 消息框处理
        processMsgBox.afterReceiveSingleMsg(answer, fromUserId);
        // 好友列表处理
        processFriendList.onReceiving(content, $receiveLi);
    },

    singleEmojiReceive: function(data) {
        data.content = getEmojiHtml(data.content);
        ws.singleReceive(data);
    },

    groupEmojiReceive: function(data) {
        data.content = getEmojiHtml(data.content);
        ws.groupReceive(data);
    },

    groupReceive: function (data) {
        // 获取、构造参数
        console.log(data);
        var fromUserId = data.sourceUid;
        var content = data.content;
        var toGroupId = data.targetGid;
        var fromAvatarUrl;
        var $receiveLi;
        $('.conLeft').find('span.hidden-userId').each(function () {
            if (this.innerHTML == fromUserId) {
                fromAvatarUrl = $(this).parent(".liRight")
                    .siblings(".liLeft").children('img').attr("src");
                /* $receiveLi = $(this).parent(".liRight").parent("li"); */
            }
        });
        $('.conLeft').find('span.hidden-groupId').each(function () {
            if (this.innerHTML == toGroupId) {
                $receiveLi = $(this).parent(".liRight").parent("li");
            }
        });
        var answer = '';
        answer += '<li>' +
            '<div class="answers">' + content + '</div>' +
            '<div class="answerHead"><img src="' + fromAvatarUrl + '"/></div>' +
            '</li>';

        // 消息框处理
        processMsgBox.afterReceiveGroupMsg(answer, toGroupId);
        // 好友列表处理
        processFriendList.onReceiving(content, $receiveLi);
    },

    fileMsgSingleReceive: function (data) {
        // 获取、构造参数
        console.log(data);
        var fromUserId = data.sourceUid;
        var originalFilename = data.fileName;
        var fileSize = data.fileSize;
        var fileUrl = data.fileURL;
        var content = "[文件]";
        var fromAvatarUrl;
        var $receiveLi;
        $('.conLeft').find('span.hidden-userId').each(function () {
            if (this.innerHTML == fromUserId) {
                fromAvatarUrl = $(this).parent(".liRight")
                    .siblings(".liLeft").children('img').attr("src");
                $receiveLi = $(this).parent(".liRight").parent("li");
            }
        })
        var fileHtml =
            '<li>' +
            '<div class="receive-file-shown">' +
            '<div class="media">' +
            '<div class="media-body"> ' +
            '<h5 class="media-heading">' + originalFilename + '</h5>' +
            '<span>' + fileSize + '</span>' +
            '</div>' +
            '<a href="' + fileUrl + '" class="media-right">' +
            '<i class="glyphicon glyphicon-file" style="font-size:28pt;"></i>' +
            '</a>' +
            '</div>' +
            '</div>' +
            '<div class="answerHead"><img src="' + fromAvatarUrl + '"/></div>' +
            '</li>';

        // 消息框处理
        processMsgBox.afterReceiveSingleMsg(fileHtml, fromUserId);
        // 好友列表处理
        processFriendList.onReceiving(content, $receiveLi);
    },

    fileMsgGroupReceive: function (data) {
        // 1. 获取、构造参数
        console.log(data);
        var fromUserId = data.sourceUid;
        var toGroupId = data.targetGid;
        var originalFilename = data.fileName;
        var fileSize = data.fileSize;
        var fileUrl = data.fileURL;
        var content = "[文件]";
        var fromAvatarUrl;
        var $receiveLi;
        $('.conLeft').find('span.hidden-userId').each(function () {
            if (this.innerHTML == fromUserId) {
                fromAvatarUrl = $(this).parent(".liRight")
                    .siblings(".liLeft").children('img').attr("src");
                /* $receiveLi = $(this).parent(".liRight").parent("li"); */
            }
        })
        $('.conLeft').find('span.hidden-groupId').each(function () {
            if (this.innerHTML == toGroupId) {
                $receiveLi = $(this).parent(".liRight").parent("li");
            }
        })
        var fileHtml =
            '<li>' +
            '<div class="receive-file-shown">' +
            '<div class="media">' +
            '<div class="media-body"> ' +
            '<h5 class="media-heading">' + originalFilename + '</h5>' +
            '<span>' + fileSize + '</span>' +
            '</div>' +
            '<a href="' + fileUrl + '" class="media-right">' +
            '<i class="glyphicon glyphicon-file" style="font-size:28pt;"></i>' +
            '</a>' +
            '</div>' +
            '</div>' +
            '<div class="answerHead"><img src="' + fromAvatarUrl + '"/></div>' +
            '</li>';

        // 2. 消息框处理
        processMsgBox.afterReceiveGroupMsg(fileHtml, toGroupId);
        // 3. 好友列表处理
        processFriendList.onReceiving(content, $receiveLi);
    },

    remove: function () {
        socket.close();
    }
};

function logout() {
    // 1. 关闭websocket连接
    ws.remove();
    // 2. 注销登录状态
    $.ajax({
        type: 'POST',
        url: 'logout',
        dataType: 'json',
        async: true,
        success: function (data) {
            // 3. 注销成功，进行页面跳转
            window.location.href = data.data;
        }
    });
    window.location.href = "/login.html";
}

$(".myfile").fileinput({
    uploadUrl: "chat/file/upload",
    uploadAsync: true, //默认异步上传
    showUpload: true, //是否显示上传按钮,跟随文本框的那个
    showRemove: false, //显示移除按钮,跟随文本框的那个
    showCaption: false,//是否显示标题,就是那个文本框
    showPreview: true, //是否显示预览,不写默认为true
    dropZoneTitle: "请通过拖拽图片文件放到这里",
    dropZoneEnabled: false,//是否显示拖拽区域，默认不写为true，但是会占用很大区域
    maxFileSize: 30720,//单位为kb，如果为0表示不限制文件大小
    maxFileCount: 1, //表示允许同时上传的最大文件个数
    enctype: 'multipart/form-data',
    validateInitialCount: true,
    previewFileIcon: "<i class='glyphicon glyphicon-file'></i>",
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
    language: 'zh'
});
//异步上传返回结果处理
$('.myfile').on('fileerror', function (event, data, msg) {
    console.log("fileerror");
    console.log(data);
});
//异步上传返回结果处理
$(".myfile").on("fileuploaded", function (event, data, previewId, index) {

    // 1. 上传成功1秒后自动关闭上传模态框
    console.log("fileuploaded");
    setTimeout(function () {
        $('#upload-cancel').trigger('click');
        $('.fileinput-remove').trigger('click');
    }, 1000);
    if (data.response.code !== 200) {
        alert(data.response.message);
        return;
    }
    // 2. 获取、设置参数
    var returnData = data.response.data;
    var originalFilename = returnData.originalFilename;
    var fileSize = returnData.fileSize;
    var fileUrl = returnData.fileUrl;
    var content = "[文件]";
    var avatarUrl = $('#avatarUrl').attr("src");
    var $sendLi = $('.conLeft').find('li.bg');
    var toUserId = $('#toUserId').val();
    var toGroupId = $('#toGroupId').val();
    var fileHtml =
        '<li>' +
        '<div class="send-file-shown">' +
        '<div class="media">' +
        '<a href="' + fileUrl + '" class="media-left">' +
        '<i class="glyphicon glyphicon-file" style="font-size:28pt;"></i>' +
        '</a>' +
        '<div class="media-body"> ' +
        '<h5 class="media-heading">' + originalFilename + '</h5>' +
        '<span>' + fileSize + '</span>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="nesHead"><img src="' + avatarUrl + '"/></div>' +
        '</li>';

    // 3. 发送信息到服务器
    if (toUserId.length !== 0) {
        ws.fileMsgSingleSend(toUserId, originalFilename, fileUrl, fileSize);
    } else {
        ws.fileMsgGroupSend(toGroupId, originalFilename, fileUrl, fileSize);
    }

    // 4. 消息框处理：
    processMsgBox.afterSendFileMsg(fileHtml, toUserId, toGroupId);

    // 5. 好友列表处理
    processFriendList.onSending(content, $sendLi);
});

//上传前
$('.myfile').on('filepreupload', function (event, data, previewId, index) {
    console.log("filepreupload");
});

// 绑定发送按钮回车事件
$('#dope').keydown(function (e) {
    if (e.keyCode === 13) {
        $('.sendBtn').trigger('click');
        e.preventDefault(); //屏蔽enter对系统作用。按后增加\r\n等换行
    }
});

// 绑定发送按钮点击事件
$('.sendBtn').on('click', function () {
    var toUserId = $('#toUserId').val();
    var toGroupId = $('#toGroupId').val();
    var news = $('#dope').val();
    if (!toUserId && !toGroupId) {
        alert("请选择对话方");
        return;
    }
    if (!news) {
        alert('消息不能为空');
    } else {
        if (toUserId.length !== 0) {
            ws.singleSend(toUserId, news);
        } else {
            ws.groupSend(toGroupId, news);
        }

        $('#dope').val('');
        var avatarUrl = $('#avatarUrl').attr("src");
        var msg = '';
        msg += '<li>' +
            '<div class="news">' + news + '</div>' +
            '<div class="nesHead"><img src="' + avatarUrl + '"/></div>' +
            '</li>';

        // 消息框处理：
        processMsgBox.afterSendMsg(msg, toUserId, toGroupId)

        // 好友列表处理：
        var $sendLi = $('.conLeft').find('li.bg');
        processFriendList.onSending(news, $sendLi);
    }
});

$('.ExP').on('mouseenter', function () {
    $('.emjon').show();
})

$('.emjon').on('mouseleave', function () {
    $('.emjon').hide();
})

$('.emjon li').on('click', function () {
    var imgSrc = $(this).children('img').attr('src');
    $('.emjon').hide();
    var toUserId = $('#toUserId').val();
    var toGroupId = $('#toGroupId').val();
    if (!toUserId  && !toGroupId) {
        alert("请选择对话方");
        return;
    }
    if (toUserId.length !== 0) {
        ws.singleEmojiSend(toUserId, imgSrc);
    } else {
        ws.groupEmojiSend(toGroupId, imgSrc);
    }
    var avatarUrl = $('#avatarUrl').attr("src");
    var content = getEmojiHtml(imgSrc);
    var msg = '';
    msg += '<li>' +
        '<div class="news">' + content + '</div>' +
        '<div class="nesHead"><img src="' + avatarUrl + '"/></div>' +
        '</li>';
    processMsgBox.afterSendMsg(msg, toUserId, toGroupId);
    var $sendLi = $('.conLeft').find('li.bg');
    content = "[emoji]";
    processFriendList.onSending(content, $sendLi);
});

function getEmojiHtml(imgSrc) {
    return '<img class="Expr" src="' + imgSrc + '">';
}

// 好友框点击事件
function friendLiClickEvent() {
    // 1. 设置点击阴影效果
    $(this).addClass('bg').siblings().removeClass('bg');

    // 2. 设置显示右侧消息框
    $('.conRight').css("display", "-webkit-box");

    // 3. 设置消息框显示对方信息，清空对方id
    var intername = $(this).children('.liRight').children('.intername').text();
    var toUserId = $(this).children('.liRight').children('.hidden-userId').text();
    var toGroupId = $(this).children('.liRight').children('.hidden-groupId').text();

    $('.headName #headName').text(intername);
    $('#hideID').text(toUserId ? toUserId : toGroupId);
    $('#toUserId').val("");
    $('#toGroupId').val("");

    // 4. 设置显示已收到的信息，设置好对方的id
    $('.newsList').html('');
    var messageArray;
    if (toUserId.length !== 0) {
        messageArray = sentMessageMap.get(toUserId);
        $('#toUserId').val(toUserId);
    } else {
        messageArray = sentMessageMap.get(toGroupId);
        $('#toGroupId').val(toGroupId);
    }
    for (var i = 0; i < messageArray.length; i++) {
        $('.newsList').append(messageArray[i]);
    }

    // 5.设置消息框滚动条滑到底部
    $('.RightCont').scrollTop($('.RightCont')[0].scrollHeight);

    // 6. 去掉红色提醒徽章
    var $badge = $(this).find(".layui-badge");
    if ($badge.length > 0) {
        $badge.remove();
    }
}

//右侧消息面板以及输入框的点击事件
function messageDivClickEvent() {
    let hideID = $('#hideID').text();
    $('.conLeft ul li').find('span.hidden-groupId,span.hidden-userId').each(function() {
        if (this.innerHTML == hideID) {
            $(this).parent().parent().trigger("click");
            //jquery return false=break, return true=continue;
            return false;
        }
    });
}

// 处理消息框的对象，统一管理相关处理函数，主要包括4个事件函数：
// (实际上应该有8个事件函数，发送得4个：单发普通信息、群发普通信息、单发文件信息、群发文件信息，
// 再加上对应的接收4个，但根据实际情况，发现代码可重用，于是便缩减为4个)
// 1. afterSendMsg: 发送(单个、群)消息时，调用此函数处理消息框变化；
// 2. afterSendFileMsg： 文件上传成功后，发送(单个、群)文件消息时，调用此函数处理消息框变化；
// 3. afterReceiveSingleMsg： 收到单发(普通对话、文件)消息时，调用此函数处理消息框变化；
// 4. afterReceiveGroupMsg： 收到群发(普通对话、文件)消息时，调用此函数处理消息框变化。
var processMsgBox = {
    afterSendMsg: function (msg, toUserId, toGroupId) {
        // 1. 把内容添加到消息框
        $('.newsList').append(msg);

        // 2. 手动计算、调整回显消息的宽度
        var $newsDiv = $('.newsList li').last().children("div").first();
        var fixWidth = 300; // 自定义的消息框本身的最长宽度
        var maxWidth = 493; // 消息框所在行(div)的满宽度(不包含头像框的宽度部分)
        var minMarginLeftWidth = 224; // 按理说应该是 maxwidth - fixWidth，这里出现了点问题
        var marginLeftWidth; // 要计算消息框的margin-left宽度
        if ($newsDiv.actual('width') < fixWidth) {
            marginLeftWidth = maxWidth - $newsDiv.actual('width');
            $newsDiv.css("margin-left", marginLeftWidth + "px");
        } else {
            $newsDiv.css("width", fixWidth + "px")
                .css("margin-left", minMarginLeftWidth + "px");
        }

        // 3. 把 调整后的消息html标签字符串 添加到已发送用户消息表
        if (toUserId.length !== 0) {
            sentMessageMap.get(toUserId).push($('.newsList li').last().prop("outerHTML"));
        } else {
            sentMessageMap.get(toGroupId).push($('.newsList li').last().prop("outerHTML"));
        }

        // 4. 滚动条往底部移
        $('.RightCont').scrollTop($('.RightCont')[0].scrollHeight);
    },

    afterSendFileMsg: function (msg, toUserId, toGroupId) {
        // 注意，文件信息消息框不需要计算宽度，已通过css设置好固定的样式
        // 1. 回显发送的新消息
        $('.newsList').append(msg);

        // 2. 把消息html标签字符串 添加到已发送用户消息表
        if (toUserId.length !== 0) {
            sentMessageMap.get(toUserId).push($('.newsList li').last().prop("outerHTML"));
        } else {
            sentMessageMap.get(toGroupId).push($('.newsList li').last().prop("outerHTML"));
        }

        // 3. 消息框往下移
        $('.RightCont').scrollTop($('.RightCont')[0].scrollHeight);
    },

    afterReceiveSingleMsg: function (msg, fromUserId) {
        // 1. 设置消息框可见
        $('.conRight').css("display", "-webkit-box");

        // 2. 把新消息放到暂存区$('.newsList-temp)，如果用户正处于与发出新消息的用户的消息框，则消息要回显
        $('.newsList-temp').append(msg);
        var $focusUserId = $(".conLeft .bg").find('span.hidden-userId');
        if ($focusUserId.length > 0 && $focusUserId.html() == fromUserId) {
            $('.newsList').append(msg);
        }

        // 3. 利用暂存区手动计算、调整新消息的宽度；
        var $answersDiv = $('.newsList-temp li').last().children("div").first();
        var fixWidth = 300; // 消息框本身的最长宽度
        var maxWidth = 480; // 消息框所在行(div)的满宽度(不包含头像框的宽度部分)
        var minMarginRightWidth = 212; // 按理说应该是 maxwidth - fixWidth，这里出现了点问题
        var marginRightWidth; // 要计算消息框的margin-right宽度
        if ($answersDiv.actual('width') < fixWidth) {
            marginRightWidth = maxWidth - $answersDiv.actual('width');
            $answersDiv.css("margin-right", marginRightWidth + "px");
            if ($focusUserId.length > 0 && $focusUserId.html() == fromUserId) {
                $('.newsList li').last().children("div").first()
                    .css("margin-right", marginRightWidth + "px");
            }
        } else {
            $answersDiv.css("width", fixWidth + "px")
                .css("margin-right", minMarginRightWidth + "px");
            if ($focusUserId.length > 0 && $focusUserId.html() == fromUserId) {
                $('.newsList li').last().children("div").first()
                    .css("width", fixWidth + "px")
                    .css("margin-right", minMarginRightWidth + "px");
            }
        }

        // 4. 把 调整后的消息html标签字符串 添加到已发送用户消息表，并清空暂存区
        sentMessageMap.get(fromUserId).push($('.newsList-temp li').last().prop("outerHTML"));
        $('.newsList-temp').empty();

        // 5. 滚动条滑到底
        $('.RightCont').scrollTop($('.RightCont')[0].scrollHeight);
    },

    afterReceiveGroupMsg: function (msg, toGroupId) {
        // 1. 设置消息框可见
        $('.conRight').css("display", "-webkit-box");

        // 2. 把新消息放到暂存区$('.newsList-temp)，如果用户正处于与发出新消息的用户的消息框，则消息要回显
        $('.newsList-temp').append(msg);
        var $focusGroupId = $(".conLeft .bg").find('span.hidden-groupId');
        if ($focusGroupId.length > 0 && $focusGroupId.html() == toGroupId) {
            $('.newsList').append(msg);
        }

        // 3. 手动计算、调整回显消息的宽度
        var $answersDiv = $('.newsList-temp li').last().children("div").first();
        var fixWidth = 300; // 消息框本身的最长宽度
        var maxWidth = 480; // 消息框所在行(div)的满宽度(不包含头像框的宽度部分)
        var minMarginRightWidth = 212; // 按理说应该是 maxwidth - fixWidth，这里出现了点问题
        var marginRightWidth; // 要计算消息框的margin-right宽度
        if ($answersDiv.actual('width') < fixWidth) {
            marginRightWidth = maxWidth - $answersDiv.actual('width');
            $answersDiv.css("margin-right", marginRightWidth + "px");
            if ($focusGroupId.length > 0 && $focusGroupId.html() == toGroupId) {
                $('.newsList li').last().children("div").first()
                    .css("margin-right", marginRightWidth + "px");
            }
        } else {
            $answersDiv.css("width", fixWidth + "px")
                .css("margin-right", minMarginRightWidth + "px");
            if ($focusGroupId.length > 0 && $focusGroupId.html() == toGroupId) {
                $('.newsList li').last().children("div").first()
                    .css("width", fixWidth + "px")
                    .css("margin-right", minMarginRightWidth + "px");
            }
        }

        // 4. 把 调整后的消息html标签字符串 添加到已发送用户消息表，并清空暂存区
        sentMessageMap.get(toGroupId).push($('.newsList-temp li').last().prop("outerHTML"));
        $('.newsList-temp').empty();

        // 5. 滚动条滑到底
        $('.RightCont').scrollTop($('.RightCont')[0].scrollHeight);
    }
}

var processFriendList = {
    onSending: function (content, $sendLi) {
        // 1. 设置部分新消息提醒
        if (content.length > 8) {
            content = content.substring(0, 8) + "...";
        }
        $('.conLeft').find('li.bg').children('.liRight').children('.infor').text(content);
        // 2. 如果存在新消息提醒徽章，则去除徽章
        if ($sendLi.find('.layui-badge').length > 0) {
            $sendLi.find('.layui-badge').remove();
        }
        //$('.conLeft ul').prepend('<li class="bg">' + $sendLi.html() + '</li>');
        // 3. 好友框新消息置顶
        $('.conLeft ul').prepend($sendLi.prop("outerHTML"));
        $sendLi.remove();
        $('.conLeft ul li').first().on('click', friendLiClickEvent)
    },

    onReceiving: function (content, $receiveLi) {
        // 1. 设置红色提醒徽章
        var $badge = $receiveLi.find(".layui-badge");
        if ($badge.length > 0) {
            $badge.html(parseInt($badge.html()) + 1);
        } else {
            var badgeHTML = '<span class="layui-badge badge-avatar">1</span>';
            $receiveLi.children(".liLeft").prepend(badgeHTML);
        }
        // 2. 设置部分新消息提醒
        if (content.length > 8) { // 只显示前八个字符
            content = content.substring(0, 8) + "...";
        }
        if (content.search("<img") !== -1) { // 若是图片，显示 “[emoji]”
            content = "[emoji]";
        }
        $receiveLi.children(".liRight").children('.infor').text(content);

        // 3. 新消息置顶
        $('.conLeft ul').prepend($receiveLi.prop("outerHTML"));
        $('.conLeft ul li').first().on('click', friendLiClickEvent);
        $receiveLi.remove();
    }
}

// 自定义数据结构：已发送用户消息表
function SentMessageMap() {
    this.elements = [];

    //获取MAP元素个数
    this.size = function () {
        return this.elements.length;
    };

    //判断MAP是否为空
    this.isEmpty = function () {
        return (this.elements.length < 1);
    };

    //删除MAP所有元素
    this.clear = function () {
        this.elements = [];
    };

    //向MAP中增加元素（key, value)
    this.put = function (_key, _value) {
        this.elements.push({
            key: _key,
            value: _value
        });
    };

    //删除指定KEY的元素，成功返回True，失败返回False
    this.removeByKey = function (_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //删除指定VALUE的元素，成功返回True，失败返回False
    this.removeByValue = function (_value) {//removeByValueAndKey
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //删除指定VALUE的元素，成功返回True，失败返回False
    this.removeByValueAndKey = function (_key, _value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //获取指定KEY的元素值VALUE，失败返回NULL
    this.get = function (_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return this.elements[i].value;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    //获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
    this.element = function (_index) {
        if (_index < 0 || _index >= this.elements.length) {
            return null;
        }
        return this.elements[_index];
    };

    //判断MAP中是否含有指定KEY的元素
    this.containsKey = function (_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //判断MAP中是否含有指定VALUE的元素
    this.containsValue = function (_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //判断MAP中是否含有指定VALUE的元素
    this.containsObj = function (_key, _value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //获取MAP中所有VALUE的数组（ARRAY）
    this.values = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].value);
        }
        return arr;
    };

    //获取MAP中所有VALUE的数组（ARRAY）
    this.valuesByKey = function (_key) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if (this.elements[i].key == _key) {
                arr.push(this.elements[i].value);
            }
        }
        return arr;
    };

    //获取MAP中所有KEY的数组（ARRAY）
    this.keys = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].key);
        }
        return arr;
    };

    //获取key通过value
    this.keysByValue = function (_value) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if (_value == this.elements[i].value) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };

    //获取MAP中所有KEY的数组（ARRAY）
    this.keysRemoveDuplicate = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            var flag = true;
            for (var j = 0; j < arr.length; j++) {
                if (arr[j] == this.elements[i].key) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };
}