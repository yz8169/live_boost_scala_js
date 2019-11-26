package tool

import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import play.api.data.JodaForms.jodaDate

/**
  * Created by yz on 2018/8/20
  */

case class UserData(name: String, password: String)

case class ResultInfo(base64: String)

case class BasicInfoData(title: String, unit: String, address: String, name: String,
                         sex: String, age: String, office: String,
                         doctor: String, number: String, sampleTime: String,
                         submitTime: String, sample: String,
                         sampleType: String, sampleStatus: String, danger: String, reporter: String, checker: String,
                         checkDate: String, reportDate: String)

case class OrignalData(sampleId: String, age: String, ast: String, alt: String, plt: String)

case class DivData(div1: String, div2: String, div3: String)

case class ExtraDataF(unit: String, address: String, name: String, sex: String, office: String, doctor: String,
                      number: String, sampleTime: String, submitTime: String, sampleType: String,
                      sampleStatus: String, title: String, reporter: String, checker: String, checkDate: String,
                      reportDate: String)

case class PdfData(score: String, result: String, svgStr: String)


class FormTool {

  val userForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(UserData.apply)(UserData.unapply)
  )

  case class AddDaysData(name: String, remainTimes: Option[Int], startTime: Option[DateTime], endTime: Option[DateTime])

  val pattern = "yyyy-MM-dd HH:mm:ss"

  val addDaysForm = Form(
    mapping(
      "name" -> text,
      "remainTimes" -> optional(number),
      "startTime" -> optional(jodaDate(pattern)),
      "endTime" -> optional(jodaDate(pattern))
    )(AddDaysData.apply)(AddDaysData.unapply)
  )

  case class RegisterUserData(name: String, fullName: String, password: String, unit: String, email: String, phone: String)

  val registerUserForm = Form(
    mapping(
      "name" -> text,
      "fullName" -> text,
      "password" -> text,
      "unit" -> text,
      "email" -> text,
      "phone" -> text
    )(RegisterUserData.apply)(RegisterUserData.unapply)
  )

  case class UserIdData(userId: Int)

  val userIdForm = Form(
    mapping(
      "userId" -> number
    )(UserIdData.apply)(UserIdData.unapply)
  )

  case class PdfInfoData(title: String, unit: String, address: String, reporter: String, checker: String)

  val pdfInfoForm = Form(
    mapping(
      "title" -> text,
      "unit" -> text,
      "address" -> text,
      "reporter" -> text,
      "checker" -> text
    )(PdfInfoData.apply)(PdfInfoData.unapply)
  )

  val pdfDataForm = Form(
    mapping(
      "score" -> text,
      "result" -> text,
      "svgStr" -> text
    )(PdfData.apply)(PdfData.unapply)
  )


  case class userNameData(name: String)

  val userNameForm = Form(
    mapping(
      "name" -> text
    )(userNameData.apply)(userNameData.unapply)
  )

  case class EmailData(email: String)

  val emailForm = Form(
    mapping(
      "email" -> text
    )(EmailData.apply)(EmailData.unapply)
  )

  case class NewPasswordData(name: String, newPassword: String)

  val newPasswordForm = Form(
    mapping(
      "name" -> text,
      "newPassword" -> text
    )(NewPasswordData.apply)(NewPasswordData.unapply)
  )

  case class PhoneData(phone: String)

  val phoneForm = Form(
    mapping(
      "phone" -> text
    )(PhoneData.apply)(PhoneData.unapply)
  )

  case class IdData(id: Int)

  val idForm = Form(
    mapping(
      "id" -> number
    )(IdData.apply)(IdData.unapply)
  )

  case class ChangePasswordData(password: String, newPassword: String)

  val changePasswordForm = Form(
    mapping(
      "password" -> text,
      "newPassword" -> text
    )(ChangePasswordData.apply)(ChangePasswordData.unapply)
  )

  case class ChangeEmailData(password: String, email: String)

  val changeEmailForm = Form(
    mapping(
      "password" -> text,
      "email" -> text
    )(ChangeEmailData.apply)(ChangeEmailData.unapply)
  )

  case class PredictData(sampleId: String, name: String, age: String, ast: String, alt: String, plt: String)

  val predictForm = Form(
    mapping(
      "sampleId" -> text,
      "name" -> text,
      "age" -> text,
      "ast" -> text,
      "alt" -> text,
      "plt" -> text
    )(PredictData.apply)(PredictData.unapply)
  )

  val basicInfoForm = Form(
    mapping(
      "title" -> text,
      "unit" -> text,
      "address" -> text,
      "name" -> text,
      "sex" -> text,
      "age" -> text,
      "office" -> text,
      "doctor" -> text,
      "number" -> text,
      "sampleTime" -> text,
      "submitTime" -> text,
      "sample" -> text,
      "sampleType" -> text,
      "sampleStatus" -> text,
      "danger" -> text,
      "reporter" -> text,
      "checker" -> text,
      "checkDate" -> text,
      "reportDate" -> text
    )(BasicInfoData.apply)(BasicInfoData.unapply)
  )

  case class MissionIdData(missionId: Int)

  val missionIdForm = Form(
    mapping(
      "missionId" -> number
    )(MissionIdData.apply)(MissionIdData.unapply)
  )

  case class FileNameData(fileName: String)

  val fileNameForm = Form(
    mapping(
      "fileName" -> text
    )(FileNameData.apply)(FileNameData.unapply)
  )

  case class MissionIdsData(missionIds: Seq[Int])

  val missionIdsForm = Form(
    mapping(
      "missionIds" -> seq(number)
    )(MissionIdsData.apply)(MissionIdsData.unapply)
  )


}
