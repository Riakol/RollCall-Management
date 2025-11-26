package com.riakol.rollcall.group.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val isFormValid = lastName.isNotBlank() && firstName.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            androidx.compose.material3.BottomSheetDefaults.DragHandle(color = TextGray)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Новый ученик",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Фамилия
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия*", color = TextGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultInputColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Имя
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя*", color = TextGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultInputColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Отчество
            OutlinedTextField(
                value = middleName,
                onValueChange = { middleName = it },
                label = { Text("Отчество (если есть)", color = TextGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultInputColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Телефон
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон", color = TextGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultInputColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена", color = TextGray, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { onSave(firstName, lastName, middleName, phone) },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = SurfaceDark,
                        contentColor = Color.White,
                        disabledContentColor = TextGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Добавить", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun defaultInputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = TextGray,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = PrimaryBlue
)