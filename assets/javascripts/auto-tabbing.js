function moveOnMax(field,nextFieldID){
  if(field.value.length >= field.max){
    document.getElementById(nextFieldID).focus();
    console.log(nextFieldID);
  }
}