 $(document).ready(function () {
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

 });