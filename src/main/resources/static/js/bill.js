/**
 * 
 */

function printBill() {
	
//	var printId = document.getElementById("printId").value;
	var printId = document.querySelector('input[id="printId"]:checked').value
	url = '/printStockOutBill';
	
	if (printId) {
		$.ajax({
			url : url,
			data : { "printId" : printId},
			success : function(result) {
				var printWindow = window.open("Print");
				printWindow.document.write(result);
				printWindow.document.close();
			}
		});
	}

}