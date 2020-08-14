$(document).ready(function () {
	$("#searchId").submit(function (event) {

		event.preventDefault();

		const $form = $(this);
		const url = $form.attr('action');

		const search = $.post(url, {
			q: $('#q').val(),
		});

		search.done(function (data) {
			showResult(data);
		});
		search.fail(function () {
			$('#result').text('failed');
		});
	});
});

function showResult(data) {
	let html = "</br>";
	for (let i = 0; i < data.content.length; i++) {
		const content = data.content[i].contents.slice(0, 100);
		html += "<p><b>" + data.content[i].title + "</b></p>";
		html += "<p><a href=\"" + data.content[i].url + "\">" + data.content[i].url + "</a></p>";
		html += "<p>" + content + " ...</p>";
		html += "</br>";
	}
	$('#result').html(html);
}