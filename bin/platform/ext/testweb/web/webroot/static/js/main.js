$(function () {
    var collapseImgUrl = $('#mainContainer').attr('data-collapseIconUrl');
    var expandImgUrl = $('#mainContainer').attr('data-expandIconUrl')

    // select/deselect all extensions
    $('#selectAllExtensions').click(function () {
        $(".extensions").trigger('click');
    });

    // Data table for all test suites
    var dTable = $('#allTestSuites').dataTable({
        "bStateSave": true,
        "iDisplayLength": 50
    })

    // select/deselect all testcases;
    $('#selectAllSuites').click(function () {
        $(".testSuite").trigger('click');
    });

    // Expand/Collapse test cases from single test suite
    var loadTestCasesUrl = $('#run-by-testsuite').attr('data-testCasesUrl');
    $('body').on("click", '.test-cases-toggle', function () {
        var toggleImg = this;
        var testSuite = $(toggleImg).attr('data-testSuite');
        var contentContainerId = $(toggleImg).attr('data-contentContainerId');

        if ($(toggleImg).attr('src') == expandImgUrl) {
            $.ajax({
                url: loadTestCasesUrl,
                type: 'POST',
                data: {'testSuite': testSuite},
                headers: {'Accept': 'application/json'},
                success: function (data) {
                    var testCaseCheckboxes = "<br />";
                    $.each(data, function () {
                        var simpleTestCaseName = this.split('#')[1].split('(')[0];
                        testCaseCheckboxes += "<input id=\"" + this + "\" name=\"testCases\" type=\"checkbox\" value=\"" + this + "\" />" +
                            "<label class=\"testCaseName\" for=\"" + this + "\">" + simpleTestCaseName + "</label><br />";
                    })

                    $('#' + contentContainerId).html(
                        testCaseCheckboxes
                    );

                    $(toggleImg).attr('src', collapseImgUrl);
                }
            })
        } else {
            $('#' + contentContainerId).html("");
            $(toggleImg).attr('src', expandImgUrl);
        }
    })

    // unchecks any checked testCase if whole test suite is checked
    $('.testSuite').click(function () {
        var contentContainerId = $(this).attr('data-contentContainerId');
        $('span#' + contentContainerId + " input:checked").each(function () {
            $(this).prop('checked', false);
        })
    })

    // toggles test cases result
    $('.testCasesContainer').hide();
    $('.toggleTestCases').click(function () {
        var testCasesContainerClass = $(this).attr('data-testCasesClass');
        $('.' + testCasesContainerClass).toggle();
        if ($(this).attr('src') == expandImgUrl) {
            $(this).attr('src', collapseImgUrl);
        } else {
            $(this).attr('src', expandImgUrl);
        }
    })

    // shows errors/infos dislog
    $('.testCaseIcon_false').click(function () {
        var stackTrace = $(this).parent().attr("data-stackTrace");

        $('#errorsContainer').html("");
        $('#errorsContainer').append(renderTestDetails(stackTrace));
        $('#errorsContainer').dialog({'title': 'Test case details', 'draggable': true, 'modal': true, 'width': 'auto'});
        $('#errorsContainer').dialog('open');
        $('#errorsContainer').dialog('moveToTop');

    }).attr('title', 'Show result');

    var renderTestDetails = function (stackTrace) {
        var container = $('<div />');
        container.attr('class', 'testCaseErrorStackTrace');

        var pre = $('<pre />');
        pre.text(stackTrace).html();

        container.append(pre);
        return container;
    }

    // overall coloring for results
    var overallResult = $('#overallResult').attr('data-result');
    if (overallResult == 'false') {
        $('#test-results-table>thead>tr').children("th").css("background-color", "#A80000");
    } else {
        $('#test-results-table>thead>tr').children("th").css("background-color", "#009933");
    }
})