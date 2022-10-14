
function populateDataIfScanCodeExists(scanCode){
	var checkCodeUrl = '/checkIfScanCodeExistsForStockIn';

	if (scanCode) {
		$.ajax({
			url : checkCodeUrl,
			data : { "scanCode" : scanCode },
			success : function(result) {
				var result = JSON.parse(result);
				if(result == null) return;

				var option = document.createElement("option");
				option.text = result[1];
				option.value = result[1];
				option.selected = 'selected';
				document.getElementById("productNames").add(option);

				var option1 = document.createElement("option");
				option1.text = result[2];
				option1.value = result[2];
				option1.selected = 'selected';
				document.getElementById("sizes").add(option1);

				var option2 = document.createElement("option");
				option2.text = result[3];
				option2.value = result[3];
				option2.selected = 'selected';
				document.getElementById("colours").add(option2);

				var option3 = document.createElement("option");
				option3.text = result[4];
				option3.value = result[0];
				option3.selected = 'selected';
				document.getElementById("brands").add(option3);

				var quantityTagPrefix = `<input type="number" class="form-control" id="quantity" placeholder="Quantity`;
				var quantityTagSuffix = `" onChange="calculateAmount()">`;
				var maxQuantityTag = `<input type="hidden" id="maxQuantity" value="`;
				$('#quantityOuter').html(quantityTagPrefix + `( `+ result[5] + ` available)` + quantityTagSuffix + maxQuantityTag + result[5] + `">`);
				
			}
		});
	}
}


$(document).ready(() => {
	document.getElementById('scanCodeStockOut').addEventListener('blur', function(event) {
		console.log('Onblur even occured for Stock Out ScanCode');
		console.log('Populating data for stock out');
		populateDataIfScanCodeExists(event.target.value);
	});

})

