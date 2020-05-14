var page = 1;
$(document).ready(function(){
    searchInfo(page);
});

function searchInfo(page){
    var ajaxbg = $("#background,#progressBar");
    var startTime = $("#dtp_input2").val();
    var endTime = $("#dtp_input3").val();
    var ower = $("#owner option:selected").val();
    var title = $("#title").val();
    var d = new Date();
    var str = '';
    str += d.getFullYear();
    str += '/';
    if (d.getMonth() + 1 <= 9){
        str += '0' + (d.getMonth() + 1);
    }else{
        str += d.getMonth() + 1;
    }
    str += '/';
    if(d.getDate() <= 9){
        str += '0' + d.getDate();
    }else{
        str += d.getDate();
    }

    if(startTime != '' && startTime != null && (endTime == '' || endTime == null)){
        alert("请输入结束时间！");
        return;
    }
    if(endTime != '' && endTime != null && (startTime == '' || startTime == null)){
        alert("请输入开始时间！");
        return;
    }
    var start = new Date(startTime.replace("-", "/").replace("-", "/"));
    var end = new Date(endTime.replace("-", "/").replace("-", "/"));
    if(start > new Date(str)){
        alert("开始时间大于今天，请重新选择！");
        return;
    }
    if(end > new Date(str)){
        alert("结束时间大于今天，请重新选择！");
        return;
    }
    if(start > end){
        alert("开始时间不能大于结束时间，请重新选择！");
        return;
    }
    var param = {
        'startTime':startTime,
        'endTime':endTime,
        'ower':ower,
        'title':title,
        'page':page
    };
    $.ajax({
        url:'serchInfo',
        data:param,
        type:'GET',
        dataType:'json',
        beforeSend:function() {
            ajaxbg.show();
        },
        success:function(data){
            var pagenation = data;
            $("#crawlerInfo").html("");
            $("#pageView").html("");
            if(pagenation != null){
                var crawlers = pagenation.crawlerContentList;
                var tableInfo = '';
                if(crawlers.length > 0){
                    var j = (pagenation.currentPage-1)*8 + 1;
                    for(var i=0; i<=crawlers.length-1; i++){
                        var id = crawlers[i].id;
                        var k = Number(j)+ Number(i);
                        tableInfo += '<tr>';
                        tableInfo += '<td width="7%">'+ k +'</td>';
                        if(crawlers[i].title == "" || crawlers[i].title == null){
                            tableInfo += '<td width="43%"></td>';
                        }else{
                            tableInfo += '<td width="43%">'+ crawlers[i].title +'</td>';
                        }
                        if(crawlers[i].time == "" || crawlers[i].time == null){
                            tableInfo += '<td width="10%"></td>';
                        }else{
                            tableInfo += '<td width="10%">'+ crawlers[i].time +'</td>';
                        }
                        if(crawlers[i].contextUrl == "" || crawlers[i].contextUrl == null){
                            tableInfo += '<td width="28%"></td>';
                        }else{
                            tableInfo += '<td width="28%"><a href="'+crawlers[i].contextUrl+'">'+crawlers[i].contextUrl+'</a></td>';
                        }
                        tableInfo += '<td width="12%"><button class="btn" onclick="detail('+crawlers[i].id+')">详情</button></td>';
                        tableInfo += '</tr>';
                    }
                }
                $("#crawlerInfo").html(tableInfo);

                //分页
                var pageInfo = '';
                if(pagenation.showFirstPage){
                    pageInfo += '<li>';
                    pageInfo += '<a href="javascript:searchInfo(1);" aria-label="Previous">';
                    pageInfo += '<span aria-hidden="true">&lt;&lt;</span>';
                    pageInfo += '</a>';
                    pageInfo += '</li>';
                }
                var currentPage = pagenation.currentPage;
                var preNum = currentPage - 1
                if(pagenation.showPrevious){
                    pageInfo += '<li>';
                    pageInfo += '<a href="javascript:searchInfo('+preNum+');" aria-label="Previous">';
                    pageInfo += '<span aria-hidden="true">&lt;</span>';
                    pageInfo += '</a>';
                    pageInfo += '</li>';
                }
                var pageSize = pagenation.pageList;
                if(pageSize.length > 0){
                    for(var k=0; k<= pageSize.length-1; k++){
                        var page2 = pageSize[k];
                        if(currentPage == page2){
                            pageInfo += '<li class="active">';
                            pageInfo += '<a href="javascript:searchInfo('+page2+');">'+ page2 +'</a>';
                            pageInfo += '</li>';
                        }else{
                            pageInfo += '<li>';
                            pageInfo += '<a href="javascript:searchInfo('+page2+');">'+ page2 +'</a>';
                            pageInfo += '</li>';
                        }
                    }
                }
                var previous = currentPage + 1;
                if(pagenation.showNext){
                    pageInfo += '<li>';
                    pageInfo += '<a href="javascript:searchInfo('+previous+');" aria-label="Previous">';
                    pageInfo += '<span aria-hidden="true">&gt;</span>';
                    pageInfo += '</a>';
                    pageInfo += '</li>';
                }
                if(pagenation.showEndPage){
                    pageInfo += '<li>';
                    pageInfo += '<a href="javascript:searchInfo('+pagenation.totalPage+');" aria-label="Previous">';
                    pageInfo += '<span aria-hidden="true">&gt;&gt;</span>';
                    pageInfo += '</a>';
                    pageInfo += '</li>';
                }
                $("#pageView").html(pageInfo);
                ajaxbg.hide();
            }else{
                ajaxbg.hide();
            }
        },
        error:function () {
            ajaxbg.hide();
            alert("出错了");
        }
    });
}

function detail(id){
    location.href = "detail?id=" + id;
}

function startCrawler(){
    var ajaxbg = $("#background,#progressBar");
    $.ajax({
        url: 'startCrawler',
        type: 'GET',
        beforeSend: function () {
            //ajaxbg.show();
        },
        success: function (data) {
            if(data == 'success'){
                alert("爬虫完毕");
                //ajaxbg.hide();
            }else{
                alert("爬虫出错");
                //ajaxbg.hide();
            }
        },
        error:function () {
            ajaxbg.hide();
            alert("出错了");
        }
    })
}

