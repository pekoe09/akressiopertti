 $(document).ready(function () {
     
     // sets up preparation time inputs as spinners
     $('#preparationMinutes').spinner({
         spin: function (event, ui) {
             if (ui.value >= 60) {
                 $(this).spinner('value', ui.value - 60);
                 $('#preparationHours').spinner('stepUp');
                 return false;
             } else if (ui.value < 0) {
                 $(this).spinner('value', ui.value + 60);
                 $('#preparationHours').spinner('stepDown');                 
                 return false;
             }            
         },
         min: 0
     });
     $('#preparationHours').spinner({
         min: 0
     });
     
     // sets up listener for adding new ingredients
     document.querySelector('#addIngredientButton').addEventListener('click', addIngredientToList, false);
     
     // fetches ingredients and sets up ingredient typeahead
     var ingredients = [];     
     var ingredientIds = [];
     $.ajax({
        url: "/ainekset/lista",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        type: "get",
        async: false,
        success: function(result){
            ingredients = result;
            $.each(result, function(index, ingredient){
               ingredientIds[ingredient.name] = ingredient.id;
            }); 
        }
    });     
     
     var ingredientMatcher = function(ingredients){
         return function findMatches(q, cb){
             var matches, substringRegex;
             matches = [];
             substringRegex = new RegExp(q, 'i');
             $.each(ingredients, function(i, ingredient) {
                 if(substringRegex.test(ingredient.name)) {
                     matches.push(ingredient);
                 }
             });
             cb(matches);
         };
     };
     
     var getIngredientName = function(ingredient){
         return ingredient.name;
     };
     
     $('#ingredientName').typeahead({
             minLength: 3,
             highlight: true
         },
         {
             name: 'ingredients',
             source: ingredientMatcher(ingredients),
             display: getIngredientName
     });
     
     $('#ingredientName').on('blur', function(evt){
        var name = $('#ingredientName').val();
        if(name.length != 0){
            $('#ingredientId').val(ingredientIds[name]);
        }
     });

     // adds listerner to package ingredient list neatly on form submit
     $('#recipeForm').submit(function(event){
        packageIngredients(); 
     });
 });
 
 function addIngredientToList(){
     var ingredientName = $("#ingredientName").val();
     var ingredientId = $("#ingredientId").val();
     var measureName = $("#ingredientMeasure option:selected").text();
     var measureId = $("#ingredientMeasure").val();
     var measureAmt = $("#ingredientAmount").val();     
     
     var amountInput = $("<input type='text' class='form-control' />").val(measureAmt);
     var amountDiv = $("<div class='col-md-2'></div>").append(amountInput);
     var measureNameField = $("<span></span>").text(measureName + " ");
     var ingredientNameField = $("<span></span>").text(ingredientName);
     var measureIdField = $("<input type='hidden' class='measureid'/>").val(measureId)
     var ingredientIdField = $("<input type='hidden' class='ingredientid' />").val(ingredientId);
     var textContainer = $("<div class='col-md-10 form-control-static'></div>").append(
            measureNameField, 
            ingredientNameField,
            measureIdField,
            ingredientIdField);
     var newIngredient = $("<div class='row ingredient-row'></div>").append(
            amountDiv, 
            textContainer);
     var newFormGroup = $("<div class='form-group'></div>").append(newIngredient);     
     $('#ingredientList').append(newFormGroup);
 }
 
 function packageIngredients(){
     var ingredientData = [];
     $('.ingredient-row').each(function(){
         var ingredientId = $(this).find('.ingredientid').val();
         var measureId = $(this).find('.measureid').val();
         var amount = $(this).find('input[type=text]').val();
         var ingredientDatum = {
             ingredientId: ingredientId,
             measureId: measureId,
             amount: amount             
         };
         ingredientData.push(ingredientDatum);
     });
     
     var ingredientJSON = JSON.stringify(ingredientData);
     $("<input type='hidden' name='ingredientSet'  />").val(ingredientJSON).appendTo("#recipeForm");
 }