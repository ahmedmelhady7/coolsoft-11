
    var marked = new Array();
    
    function step1(){
        document.getElementById('panel5').style.display = 'none';
        document.getElementById('panel2').style.display = 'none';
        document.getElementById('tabholder_articles').style.display = 'none';
        document.getElementById('load').innerHTML = '<center><img src='/public/images/Searching.gif'/></center>';
        setTimeout('showNewPanel(5)', 2500);
    }
    
    function check(){
        if (document.getElementById('O').checked == false) {
            document.getElementById('divO').style.display = 'none';
        }
        else {
            document.getElementById('divO').style.display = 'block';
        }
        if (document.getElementById('O').checked == false) {
            document.getElementById('divO1').style.display = 'none';
        }
        else {
            document.getElementById('divO1').style.display = 'block';
        }
        if (document.getElementById('T').checked == false) {
            document.getElementById('divT').style.display = 'none';
        }
        else {
            document.getElementById('divT').style.display = 'block';
        }
        if (document.getElementById('T').checked == false) {
            document.getElementById('divT1').style.display = 'none';
        }
        else {
            document.getElementById('divT1').style.display = 'block';
        }
        if (document.getElementById('E').checked == false) {
            document.getElementById('divE').style.display = 'none';
        }
        else {
            document.getElementById('divE').style.display = 'block';
        }
        if (document.getElementById('E').checked == false) {
            document.getElementById('divE1').style.display = 'none';
        }
        else {
            document.getElementById('divE1').style.display = 'block';
        }
        if (document.getElementById('I').checked == false) {
            document.getElementById('divI').style.display = 'none';
        }
        else {
            document.getElementById('divI').style.display = 'block';
        }
        if (document.getElementById('I').checked == false) {
            document.getElementById('divI1').style.display = 'none';
        }
        else {
            document.getElementById('divI1').style.display = 'block';
        }
        if (document.getElementById('P').checked == false) {
            document.getElementById('divP').style.display = 'none';
        }
        else {
            document.getElementById('divP').style.display = 'block';
        }
        if (document.getElementById('P').checked == false) {
            document.getElementById('divP1').style.display = 'none';
        }
        else {
            document.getElementById('divP1').style.display = 'block';
        }
        if (document.getElementById('It').checked == false) {
            document.getElementById('divIt').style.display = 'none';
        }
        else {
            document.getElementById('divIt').style.display = 'block';
        }
        if (document.getElementById('It').checked == false) {
            document.getElementById('divIt1').style.display = 'none';
        }
        else {
            document.getElementById('divIt1').style.display = 'block';
        }
    }
    
    var currentPanel = 5;
    
    function showNewPanel(panelNum){
        if (currentPanel != null) {
            hidePanel();
        }
        document.getElementById('load').style.display = 'none';
        document.getElementById('panel' + panelNum).style.display = 'block';
        document.getElementById('tabholder_articles').style.display = 'block';
        currentPanel = panelNum;
        setState(panelNum);
        $('#panel' + panelNum).hide();
        $('#panel' + panelNum).show(1000);
        check();
    }
    
    function hidePanel(){
        document.getElementById('panel' + currentPanel).style.display = 'none';
        document.getElementById('tab' + currentPanel).className = 'inactiveTab';
        document.getElementById('tab' + currentPanel).style.color = '#ffffff';
        document.getElementById('tab' + currentPanel).style.background = '#000000';
    }
    
    function setState(tabNum){
        if (tabNum == currentPanel) {
            document.getElementById('tab' + tabNum).style.color = '#000000';
            document.getElementById('tab' + tabNum).style.background = '#ffffff';
            document.getElementById('tab' + tabNum).className = 'activeTab';
        }
        else {
            document.getElementById('tab' + tabNum).style.color = '#ffffff';
            document.getElementById('tab' + tabNum).style.background = '#000000';
            document.getElementById('tab' + tabNum).className = 'inactiveTab';
        }
    }
    
    function hover(tab){
        tab.style.color = '#000000';
        tab.style.background = '#ffffff';
    }
    
    function quickSearch(frm){
        $.post('@{quickSearch}', {
            keyword: frm.search.value
        }, function(){
            window.location.reload(true);
        });
    }
    
    function whichsortmethod(frm){
        var c = 'v';
        var e = 'r';
        if (document.getElementById('s1').checked) {
            //			if (document.getElementById('v').checked) {
            //document.write(document.sort.v.name);
            $.post('@{sortD}', { //voteOrRate : document.sort.v.value
    }, function(){
                window.location.reload(true);
            });
            
        }
        else //if (document.getElementById('v').checked)
        {
            $.post('@{sortA}', { //voteOrRate : document.sort.v.value
    }, function(){
                window.location.reload(true);
            });
        }
    }
    
    function mark(id){
        if (marked.indexOf(id) == -1 && marked.length < 2) {
            marked.push(id);
        }
        else {
            if (marked.indexOf(id) != -1) {
                marked.pop(id);
            }
            else {
                marked.push(id);
                alert('you could only mark two ideas');
                for (i = 0; i < marked.length; i++) {
                    document.getElementById(marked[i] + '').checked = false;
                }
                marked = new Array();
            }
        }
    }
    
    function markDuplicate(){
        if (marked.length < 2) {
            alert('Mark two ideas');
        }
        else {
            $.post('@{MarkRequest.sendRequest}', {
                idea1ID: marked[0],
                idea2ID: marked[1],
                des: 'Sending a request to mark'
            }, function(){
                alert('The requests have been sent to the Topic organizer');
                for (i = 0; i < marked.length; i++) {
                    document.getElementById(marked[i] + '').disabled = true;
                    document.getElementById(marked[i] + '').checked = false;
                }
                marked = new Array();
            });
        }
    }