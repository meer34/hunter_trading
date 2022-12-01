/**
 * 
 */

function printBill() {
	
	var contact = $("#contact option:selected").map(function () {return $(this).text();}).get().join(', ');
	if(contact == '') {
		alert("Please select a seller contact for billing!");
		return;
	}
	var printId = document.querySelector('input[id="printId"]:checked').value,
	url = '/printStockOutBill';
	
	if (printId) {
		$.ajax({
			url : url,
			data : {"printId" : printId, "contact" : contact},
			success : function(result) {
				var printWindow = window.open("Print");
				printWindow.document.write(result);
				printWindow.document.close();
			}
		});
	}

}