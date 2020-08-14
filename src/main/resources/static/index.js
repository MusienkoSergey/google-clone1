$(document).ready(function () {
	$("#indexId").submit(function (event) {

		event.preventDefault();

		const $form = $(this);
		const url = $form.attr('action');

		const search = $.post(url, {
			q: $('#q').val(),
			depth: $('#depth').val()
		});

		search.done(function (data) {
			$('#result').text('success');
		});
		search.fail(function () {
			$('#result').text('failed');
		});
	});
});