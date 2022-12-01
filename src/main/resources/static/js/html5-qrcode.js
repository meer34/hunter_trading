
function populateDataIfScanCodeExists(scanCode){
	var checkCodeUrl = '/checkIfScanCodeExistsForStockIn';

	if (scanCode) {
		$.ajax({
			url : checkCodeUrl,
			data : { "scanCode" : scanCode },
			success : function(result) {
				if(result == null || result == '') {
					var sizeTag = '<option selected disabled="disabled" value="">Size</option>';
					var colourTag = '<option selected disabled="disabled" value="">Colour</option>';
					var brandTag = '<option selected disabled="disabled" value="">Brand</option>';
					var productTag = '<option selected disabled="disabled" value="">Product Type</option>';
					var quantityTag = '<input type="number" class="form-control" id="quantity" placeholder="Quantity" onChange="calculateAmount()">';
					var rateTag = '<input type="number" class="form-control" id="rate" placeholder="Rate" onChange="calculateAmount()">';
					var amountTag = '<input type="number" class="form-control" id="amount" placeholder="Amount" readonly>';
					
					$('#sizes').html(sizeTag);
					$('#colours').html(colourTag);
					$('#brands').html(brandTag);
					$('#productTag').html(productTag);
					$('#quantity').html(quantityTag);
					$('#rate').html(rateTag);
					$('#amount').html(amountTag);
					return;
				}
				
				var result = JSON.parse(result);

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
				option3.value = result[4];
				option3.selected = 'selected';
				document.getElementById("brands").add(option3);
				
				var option4 = document.createElement("option");
				option4.text = result[5];
				option4.value = result[0];
				option4.selected = 'selected';
				document.getElementById("productTypes").add(option4);
				
				$('#productCode').val(result[6]);
				$('#mrp').val(result[7]);
				$('#sellRate').val(result[8]);

				var quantityTagPrefix = `<input type="number" class="form-control" id="quantity" placeholder="Quantity`;
				var quantityTagSuffix = `" onChange="calculateAmount()">`;
				var maxQuantityTag = `<input type="hidden" id="maxQuantity" value="`;
				$('#quantityOuter').html(quantityTagPrefix + `( `+ result[9] + ` available)` + quantityTagSuffix + maxQuantityTag + result[9] + `">`);
				
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

