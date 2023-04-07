# Cheat Sheet

## WPF Bindings

```csharp
<Button Height="30" Command="{Binding CmdSaveChanges}">Änderung speichern</Button>

<TextBox Text="{Binding FirstName}"></TextBox>

<TextBox Text="{Binding LastName, UpdateSourceTrigger=PropertyChanged}"></TextBox>

<TextBlock Text="{Binding Path=DateWithMaxDraws, StringFormat='Die meisten Unentschieden am {0:dd.MM.yyyy}'}" VerticalAlignment="Center" HorizontalAlignment="Right">

 <Label Content="{Binding AvgPrice}"></Label>

<ListView SelectedItem="{Binding SelectedEmp}" Grid.Row="2" ItemsSource="{Binding Employees}">
    <ListView.View>
        <GridView>
            <GridViewColumn Width="120" Header="Vorname" DisplayMemberBinding="{Binding FirstName}"></GridViewColumn>
        </GridView>
    </ListView.View>
</ListView>

<ComboBox ItemsSource="{Binding Owners}" DisplayMemberPath="Name" SelectedValuePath="Id" Name="cbOwner" SelectedItem="{Binding SelectedOwnerInFilter}"></ComboBox>

<DatePicker SelectedDate="{Binding NewGarden.RentAt}" Grid.Row="2" Grid.Column="1"></DatePicker>


```

## Connection zu .cs with normal Field 


```csharp
private DateTime _dateWithMaxDraws;

        public DateTime DateWithMaxDraws
        {
            get { return _dateWithMaxDraws; }
            set
            {
               SetProperty(ref _dateWithMaxDraws, value);
            }
        }

private ObservableCollection<Team> _teams = new();

        public ObservableCollection<Team> Teams
        {
            get { return _teams; }
            set { SetProperty(ref _teams, value); }
```

## Auf Änderungen reagieren

Wenn man z.B. wie bei dem GardenPlanner Beispiel in einer ListView ein Element auswählt, und anhand von der Auswahl wird eine andere ListView refresehd, dann macht man das im Setter:

```csharp
private Tenant? _selectedOwnerInFilter;

        public Tenant? SelectedOwnerInFilter
        {
            get { return _selectedOwnerInFilter; }
            set { 
                SetProperty(ref _selectedOwnerInFilter, value);

                _ = RefreshGardenListAsync();

            }
        }
```

## Connection zu .cs with command

```csharp
public RelayCommand CmdFilter { get; set; }

CmdFilter = new RelayCommand(
                async o =>
                {
                        if (!string.IsNullOrEmpty(FilterTeam)) //Sicherheitshalber
                        {
                            using (UnitOfWork uow = new UnitOfWork())
                            {
                                Teams = new ObservableCollection<Team>(await uow.TeamRepository.GetWithFilterAsync(FilterTeam));
                            }
                            //_filterActive = true;
                        }
                    
                },
                o =>
                {
                    return !string.IsNullOrEmpty(FilterTeam);
                });

```
In der ersten Lambda Expression programmiert man was gemacht wird, wenn man auf den Button klickt (angenommen man darf darauf klicken)

Hier wird bei der zweiten Lambda Expression geprüft ob der Button klickbar sein soll (true/false)

## Neues Window öffnen

```csharp
if (selectedTeam != null) //Sicherheitshalber
{
    await WindowController.ShowWindowAsync(new TeamDetailViewModel(WindowController, selectedTeam));
    await LoadAllTeamsAsync();
}

```

1. Es muss dann ein neues WPF Window gemacht werden.
2. Es muss ein neues ViewModel erstellt werden
3. Muss in WindowController.cs geadded werden

```csharp
Window? window=null;

            if (baseViewModel is MainViewModel)
            {
                window = new MainWindow();
            }
            else if(baseViewModel is DetailTeamViewModel)
            {
                window = new DetailTeamWindow();
            }
```

4. Das neue ViewModel muss von BaseViewModel erben

## Window schließen

```csharp
CmdCancel = new RelayCommand(o =>
            {
                Controller.CloseWindow(this);
            },
            o =>
            {
                return true;
            });
```

## Format of a date

```csharp
<GridViewColumn Header="Datum" DisplayMemberBinding="{Binding Path=Date, StringFormat={}{0:dd.MM.yyyy}}"/>

```

## UpdateSourceTrigger


```csharp
<TextBox Text="{Binding TeamName, UpdateSourceTrigger=PropertyChanged}" Width="200"></TextBox>

```
Es wird nach jeder Eingabe geprüft, ob die Bedingung die beispielsweise bei einem Button ist noch zutrifft.


## Datagrid

```csharp
<DataGrid Grid.Row="1" CanUserAddRows="False" AutoGenerateColumns="False" ItemsSource="{Binding Activities}">
    <DataGrid.Columns>
            <DataGridTextColumn Header="Datum" Binding="{Binding Date, StringFormat=dd.MM.yyyy}" IsReadOnly="True"></DataGridTextColumn>
    </DataGrid.Columns>
</DataGrid>
```

# Validations

## Entity mit Error Message

### Im App.xaml
```csharp
<Style TargetType="TextBox">
            <Style.Triggers>
                <Trigger Property="Validation.HasError" Value="true">
                    <Setter Property="ToolTip"
                        Value="{Binding RelativeSource={x:Static RelativeSource.Self},
                        Path=(Validation.Errors)[0].ErrorContent}"/>
                </Trigger>
            </Style.Triggers>
            <Setter Property="Margin" Value="5,5,5,5"></Setter>
        </Style>
```

### Im ViewModel.cd

```csharp
[Required(ErrorMessage = "Team Name muss eingegeben werden")]
        [MinLength(3, ErrorMessage = "Name muss mindestens 3 Zeichen haben")]
        public string TeamName
        {
            get { return _teamName; }
            set
            {
                SetProperty(ref _teamName, value);
            }
        }

```

### Im Window.xaml

```csharp
<TextBox Text="{Binding Path=TeamName, UpdateSourceTrigger=PropertyChanged}" Width="150"></TextBox>
```


## Eigenes Validation Attribute

Im Entities Ordner muss ein Validations Ordner angelegt werden.
Der Rest steht in den normalen Validierungsfolien ab Folie 6.
Es gibt verschiedene Möglichkeiten:

### ViewModel.cs
```csharp
[TeamValidations]
        public string TeamName
        {
            get { return _teamName; }
            set
            {
                SetProperty(ref _teamName, value);
            }
        }

```


## Validation von DB Layer

### UnitOfWork

```csharp
private void ValidateEntity(object entity)
        {
            if (entity is Team team)
            {
                if (_dbContext.Teams.Any(t => (t.TeamName.ToUpper() == team.TeamName) && (t.Id != team.Id)))
                {
                    throw new ValidationException($"Es gibt bereits ein Team mit dem Namen " + team.TeamName,
                        null, nameof(Team.TeamName));
                }
            }
        }

```

### ViewModel.cs

```csharp
if(!HassErrors){
try{
    DbError = "";
    ...
}
catch (ValidationException ex)
                             {
                                 DbError = ex.ValidationResult.ErrorMessage;
                             }
}

```

### App.xaml

```csharp
<Style x:Key="ErrorFontColorStyle" TargetType="TextBlock">
            <Setter Property="Foreground" Value="Red"/>
        </Style>

```

### Window.xaml

```csharp
<TextBlock TextWrapping="Wrap" Grid.Row="4" MinHeight="30" Grid.ColumnSpan="2" 
                   Style="{StaticResource ErrorFontColorStyle}" Text="{Binding DbError}"></TextBlock>

```


# Neue Migration nach Änderung von Entities

* Migrations Ordner löschen
* Aus dem SQL Server Object Explorer die DB löschen
* Auf Persistence stellen
* Erstellen der DB mit: add-migration initialMigrate
* Updaten der DB mit : update-database
