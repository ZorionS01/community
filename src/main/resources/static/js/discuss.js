$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn,entityType,entityId,entityUserId,postId) {
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId
        ,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
                // window.location.reload();
            }else {
                alert(data.msg);
            }
        }
    );
}
//置顶、取消置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code ==0){
                $("#topBtn").text(data.type == 1?'取消置顶':'置顶');
            }else {
                alert(data.msg);
            }
        }

    );
}
//加精、取消加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code ==0){
                // $("#wonderfulBtn").attr("disabled","disabled");
                $("#wonderfulBtn").text(data.status == 1?'取消加精':'加精');
            }else {
                alert(data.msg);
            }
        }

    );
}
//删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                // alert("成功删除");
                location.href = CONTEXT_PATH + "/index";
                // alert("跳转成功");
            }else {
                alert(data.msg);
            }
        }

    );
}
